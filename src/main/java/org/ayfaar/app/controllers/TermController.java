package org.ayfaar.app.controllers;

import org.apache.commons.lang.WordUtils;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.*;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.Morpher;
import org.ayfaar.app.utils.TermUtils;
import org.ayfaar.app.utils.ValueObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.ayfaar.app.model.Link.*;
import static org.ayfaar.app.utils.ValueObjectUtils.convertToPlainObjects;
import static org.ayfaar.app.utils.ValueObjectUtils.getModelMap;
import static org.springframework.util.Assert.notNull;

@Controller
@RequestMapping("term")
public class TermController {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(TermController.class.getName());

    @Autowired CommonDao commonDao;
    @Autowired TermDao termDao;
    @Autowired LinkDao linkDao;
    @Autowired AliasesMap aliasesMap;

    /*@RequestMapping("import")
    @Model
    public void _import() throws ParserConfigurationException, SAXException, IOException {
        for (Termin termin : commonDao.getAll(Termin.class)) {
            add(termin.getName());
        }
    }*/

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @Model
    public void add(Term term) {
        add(term.getName(), term.getShortDescription(), term.getDescription());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Model
    public ModelMap get(@RequestParam("name") String termName) {
        Term term = termDao.getByName(termName);
        notNull(term, "Термин не найден");
        return get(term);
    }

    public ModelMap get(Term term) {
        String termName = term.getName();
        Term alias = null;
        if (!aliasesMap.get(termName).getTerm().getUri().equals(term.getUri())) {
            alias = term;
            term = aliasesMap.get(termName).getTerm();
        }

//        Matcher matcher = Pattern.compile("^[А-ЯЁ]+$").matcher(termName);
//        if (matcher.find()) {
        // может быть аббравиатурой или сокращением
        Link _link = linkDao.getForAbbreviation(term.getUri());
        if (_link != null && _link.getUid1() instanceof Term) {
            alias = term;
            term = (Term) _link.getUid1();
        }
//        }

        ModelMap modelMap = (ModelMap) getModelMap(term);

//        List<UID> aliases = new ArrayList<UID>();

//        for (Link link : linkDao.getAliases(term.getUri())) {
//            aliases.add(link.getUid2());
//        }

        modelMap.put("from", alias);
//        modelMap.put("related", getRelated(term.getUri()));
//        modelMap.put("aliases", aliases);

        // LINKS

        List<ModelMap> quotes = new ArrayList<ModelMap>();
        Set<UID> related = new LinkedHashSet<UID>();
        Set<UID> aliases = new LinkedHashSet<UID>();

        UID code = null;
        for (Link link : linkDao.getAllLinks(term.getUri())) {
            UID source = link.getUid1().getUri().equals(term.getUri())
                    ? link.getUid2()
                    : link.getUid1();
            if (link.getQuote() != null || source instanceof Item) {
                ModelMap map = new ModelMap();
                map.put("quote", link.getQuote() != null ? link.getQuote() : ((Item) source).getContent());
                map.put("uri", source.getUri());
                quotes.add(map);
            } else if (ABBREVIATION.equals(link.getType()) || ALIAS.equals(link.getType())) {
                aliases.add(source);
            } else if (CODE.equals(link.getType())) {
                code = source;
            } else {
                related.add(source);
            }
        }
        modelMap.put("code", code);
        modelMap.put("quotes", quotes);
        modelMap.put("related", convertToPlainObjects(related, new ValueObjectUtils.Modifier<UID>() {
            @Override
            public void modify(UID entity, ModelMap map) {
                map.remove("content");
            }
        }));
        modelMap.put("aliases", convertToPlainObjects(aliases, new ValueObjectUtils.Modifier<UID>() {
            @Override
            public void modify(UID entity, ModelMap map) {
                map.remove("content");
            }
        }));

        return modelMap;
    }

    @RequestMapping("related")
    @Model
    @ResponseBody
    public Collection<ModelMap> getRelated(@RequestParam String uri) {
        Set<UID> related = new LinkedHashSet<UID>();
        for (Link link : linkDao.getRelated(uri)) {
            if (!ABBREVIATION.equals(link.getType()) && link.getQuote() == null) {
                if (link.getUid1().getUri().equals(uri)) {
                    related.add(link.getUid2());
                } else {
                    related.add(link.getUid1());
                }
            }
        }
        return convertToPlainObjects(related, new ValueObjectUtils.Modifier<UID>() {
            @Override
            public void modify(UID entity, ModelMap map) {
                map.remove("content");
            }
        });
    }

//    @RequestMapping(value = "/", method = POST)
//    @Model
    public Term add(String name, String description) {
        return add(name, null, description);
    }

    public Term add(String name, String shortDescription, String description) {
        name = name.replace("\"", "").replace("«", "").replace("»", "").trim();
        name = WordUtils.capitalize(name, new char[]{'@'}); // Делаем первую букву большой, @ - знак который не появляеться в названии, чтобы поднялась только первая буква всей фразы
        Term primeTerm = termDao.getByName(name);
        if (primeTerm == null) {
            primeTerm = termDao.save(new Term(name, shortDescription, description));
            String termName = primeTerm.getName();
            log.info("Added: "+ termName);
            if (TermUtils.isComposite(termName)) {
                String target = TermUtils.getNonCosmicCodePart(termName);
                if (target != null) {
                    findAliases(primeTerm, target, termName.replace(target, ""));
                }
            } else if (!TermUtils.isCosmicCode(termName)) {
                findAliases(primeTerm, termName, "");
            }
        }
        aliasesMap.reload();
        return primeTerm;
    }

    private void findAliases(Term primeTerm, String target, String prefix) {
        Morpher morpher = null;
        try {
            morpher = new Morpher(target);
            if (morpher.getData()) {
                Set<String> aliases = new HashSet<String>();
                for (Morpher.Morph morph : morpher.getAllMorph()) {
                    if (morph == null) {
                        return;
                    }
                    String alias = prefix+morph.text;
                    if (morph != null && !alias.isEmpty()
                            && !alias.equals(primeTerm.getName())) {

                        if (!aliases.contains(alias)) {
                            TermMorph termMorph = commonDao.get(TermMorph.class, alias);
                            if (termMorph == null) {
                                commonDao.save(new TermMorph(alias, primeTerm.getUri()));
                                aliasesMap.put(alias, primeTerm);
                                log.info("Alias added: "+alias);
                            }
                            aliases.add(alias);
                        }
                    }
                }
               /* for (Map.Entry<String, Term> entry : aliases.entrySet()) {
                    if (primeTerm.getUri().equals(entry.getValue().generateUri())) continue;
                    commonDao.save(new Link(primeTerm, entry.getValue(), Link.ALIAS, Link.MORPHEME_WEIGHT));
                }*/
            }
        } catch (Exception e) {
            log.throwing(getClass().getName(), "findAliases", e);
            throw new RuntimeException(e);
        }
    }



    public Term getPrime(Term term) {
        return (Term) linkDao.getPrimeForAlias(term.getUri());
    }

    public Term add(String termName) {
        return add(termName, null);
    }

    @RequestMapping("autocomplete")
    @ResponseBody
    public List<String> autoComplete(@RequestParam("filter[filters][0][value]") String filter) {
        List<Term> terms = termDao.getLike("name", /*"%" +*/ filter + "%");
        List<String> names = new ArrayList<String>();
        for (Term term : terms) {
            names.add(term.getName());
        }
        return names;
    }

    @RequestMapping("remove/{name}")
    public void remove(@PathVariable String name) {
        termDao.remove(termDao.getByName(name).getUri());
    }
}
