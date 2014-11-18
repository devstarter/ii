package org.ayfaar.app.controllers;

import org.apache.commons.lang.WordUtils;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.*;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static org.ayfaar.app.model.Link.*;
import static org.ayfaar.app.utils.ValueObjectUtils.convertToPlainObjects;
import static org.ayfaar.app.utils.ValueObjectUtils.getModelMap;
import static org.springframework.util.Assert.notNull;

@Controller
@RequestMapping("api/term")
public class TermController {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(TermController.class.getName());

    @Autowired CommonDao commonDao;
    @Autowired TermDao termDao;
    @Autowired LinkDao linkDao;
    @Autowired TermsMap termsMap;
    @Autowired NewAliasesMap aliasesMap;
    @Autowired SuggestionsController searchController2;
    @Inject TermsMarker termsMarker;

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
    public ModelMap get(@RequestParam("name") String termName, @RequestParam(required = false) boolean mark) {
        Term term = termDao.getByName(termName);
        notNull(term, "Термин не найден");
        return get(term, mark);
    }

    public ModelMap get(Term term) {
        return get(term, false);
    }

    public ModelMap get(Term term, boolean mark) {
        String termName = term.getName();
        Term alias = null;
        if (!termsMap.getTermProvider(termName).getUri().equals(term.getUri())) {
            alias = term;
            term = termsMap.getTerm(termName);
        }

        // может быть аббравиатурой, сокращением, кодом или синонимов
        Link _link = linkDao.getForAbbreviationOrAliasOrCode(term.getUri());
        if (_link != null && _link.getUid1() instanceof Term) {
            alias = term;
            term = (Term) _link.getUid1();
        }
//        }

        ModelMap modelMap = (ModelMap) getModelMap(term);

        modelMap.put("from", alias);

        // LINKS

        List<ModelMap> quotes = new ArrayList<ModelMap>();
        Set<UID> related = new LinkedHashSet<UID>();
        Set<UID> aliases = new LinkedHashSet<UID>();

        UID code = null;
        List<Link> links = linkDao.getAllLinks(term.getUri());
        for (Link link : links) {
            UID source = link.getUid1().getUri().equals(term.getUri())
                    ? link.getUid2()
                    : link.getUid1();
            if (link.getQuote() != null || source instanceof Item) {
                quotes.add(getQuote(link, source, mark));
            } else if (ABBREVIATION.equals(link.getType()) || ALIAS.equals(link.getType())) {
                aliases.add(source);
            } else if (CODE.equals(link.getType())) {
                code = source;
            } else {
                related.add(source);
            }
        }

        // Нужно также включить цитаты всех синонимов и сокращений и кода
        Set<UID> aliasesQuoteSources = new HashSet<UID>(aliases);
        if (code != null) {
            aliasesQuoteSources.add(code);
        }
        for (UID uid : aliasesQuoteSources) {
            List<Link> aliasLinksWithQuote = linkDao.getAllLinks(uid.getUri());
            for (Link link : aliasLinksWithQuote) {
                UID source = link.getUid1().getUri().equals(uid.getUri())
                        ? link.getUid2()
                        : link.getUid1();
                if (link.getQuote() != null || source instanceof Item) {
                    quotes.add(getQuote(link, source, mark));
                } else if (ABBREVIATION.equals(link.getType()) || ALIAS.equals(link.getType()) || CODE.equals(link.getType())) {
                    // Синонимы синонимов :) по идее их не должно быть, но если вдруг...
                    // как минимум один есть и этот наш основной термин
                    if (!source.getUri().equals(term.getUri())) {
                        aliases.add(source);
                    }
                } else {
                    related.add(source);
                }
            }
        }
        sort(quotes, new Comparator<ModelMap>() {
            @Override
            public int compare(ModelMap o1, ModelMap o2) {
                return ((String) o1.get("uri")).compareTo((String) o2.get("uri"));
            }
        });

        modelMap.put("code", code);
        modelMap.put("quotes", quotes);
        modelMap.put("related", toPlainObjectWithoutContent(related));
        modelMap.put("aliases", toPlainObjectWithoutContent(aliases));

        if (mark) {
            for (String p : asList("description", "shortDescription")) {
                modelMap.put(p, termsMarker.mark((String) modelMap.get(p)));
            }
        }

        return modelMap;
    }

    private Object toPlainObjectWithoutContent(Set<UID> related) {
        return convertToPlainObjects(related, new ValueObjectUtils.Modifier<UID>() {
            @Override
            public void modify(UID entity, ModelMap map) {
                map.remove("content");
            }
        });
    }

    private ModelMap getQuote(Link link, UID source, boolean mark) {
        ModelMap map = new ModelMap();
        String quote = link.getQuote() != null ? link.getQuote() : ((Item) source).getContent();
        if (mark) quote = termsMarker.mark(quote);
        map.put("quote", quote);
        map.put("uri", source.getUri());
        return map;
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
        Term term = termDao.getByName(name);
        if (term == null) {
            term = termDao.save(new Term(name, shortDescription, description));
            String termName = term.getName();
            log.info("Added: "+ termName);
            if (TermUtils.isComposite(termName)) {
                String target = TermUtils.getNonCosmicCodePart(termName);
                if (target != null) {
                    findAliases(term, target, termName.replace(target, ""));
                }
            } else if (!TermUtils.isCosmicCode(termName)) {
                findAliases(term, termName, "");
            }
        } else {
            if (shortDescription != null && !shortDescription.isEmpty()) term.setShortDescription(shortDescription);
            if (description != null && !description.isEmpty()) term.setDescription(description);
            termDao.save(term);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                termsMap.reload();
            }
        }).start();

        return term;
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

                                TermsMap.TermProvider provider = aliasesMap.new TermProviderImpl(
                                        primeTerm.getUri(), null, primeTerm.getShortDescription() != null);

                                termsMap.put(alias, provider);
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
        return searchController2.suggestions(filter);
    }

    @RequestMapping("get-short-description")
    @ResponseBody
    public String getShortDescription(@RequestParam String name) {
        return termsMap.getTerm(name).getShortDescription();
    }

    @RequestMapping("remove/{name}")
    public void remove(@PathVariable String name) {
        termDao.remove(termDao.getByName(name).getUri());
    }


    @RequestMapping("reload-aliases-map")
    public void reloadAliasesMap() {
        termsMap.reload();
    }
}
