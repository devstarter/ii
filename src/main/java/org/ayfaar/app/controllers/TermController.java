package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.UID;
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

import static org.ayfaar.app.utils.ValueObjectUtils.convertToPlainObjects;
import static org.ayfaar.app.utils.ValueObjectUtils.getModelMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/term")
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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Model
//    @Cacheable("terms")
    public ModelMap altGet(@RequestParam String name) {
        return get(name);
    }

    @RequestMapping("{termName}")
    @Model
    public ModelMap get(@PathVariable String termName) {
        Term term = termDao.getByName(termName);
        notNull(term, "Термин не найден");

        Term alias = null;
        if (!aliasesMap.get(termName).getPrime().getUri().equals(term.getUri())) {
            alias = term;
            term = aliasesMap.get(termName).getPrime();
        }

        ModelMap modelMap = (ModelMap) getModelMap(term);

//        List<UID> aliases = new ArrayList<UID>();

//        for (Link link : linkDao.getAliases(term.getUri())) {
//            aliases.add(link.getUid2());
//        }

        modelMap.put("from", alias);
//        modelMap.put("related", getRelated(term.getUri()));
//        modelMap.put("aliases", aliases);

        return modelMap;
    }

    @RequestMapping("related")
    @Model
    @ResponseBody
//    @Cacheable("terms")
    public Collection<ModelMap> getRelated(@RequestParam String uri) {
        Set<UID> related = new LinkedHashSet<UID>();
        for (Link link : linkDao.getRelated(uri)) {
            if (link.getUid1().getUri().equals(uri)) {
                related.add(link.getUid2());
            } else {
                related.add(link.getUid1());
            }
        }
        return convertToPlainObjects(related, new ValueObjectUtils.Modifier<UID>() {
            @Override
            public void modify(UID entity, ModelMap map) {
                map.remove("content");
            }
        });
    }

    @RequestMapping(value = "/", method = POST)
    @Model
//    @CacheEvict(value = {"items", "terms"}, allEntries=true)
    public Term add(@RequestParam String name, @RequestParam(required = false) String description) {
        Term primeTerm = termDao.getByName(name);
        if (primeTerm == null) {
            primeTerm = termDao.save(new Term(name, description));
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
        return primeTerm;
    }

    private void findAliases(Term primeTerm, String target, String prefix) {
        Morpher morpher = null;
        try {
            morpher = new Morpher(target);
            if (morpher.getData()) {
                Map<String, Term> aliases = new HashMap<String, Term>();
                for (Morpher.Morph morph : morpher.getAllMorph()) {
                    if (morph == null) {
                        return;
                    }
                    String alias = prefix+morph.text;
                    if (morph != null && !alias.isEmpty()
                            && !alias.equals(primeTerm.getName())) {

                        if (aliases.get(alias) == null) {
                            Term aliasTerm = termDao.getByName(alias);
                            if (aliasTerm == null) {
                                aliasTerm = termDao.save(new Term(alias));
                                log.info("Alias added: "+aliasTerm.getName());
                            }
                            aliases.put(alias, aliasTerm);
                        }
                    }
                }
                for (Map.Entry<String, Term> entry : aliases.entrySet()) {
                    if (primeTerm.getUri().equals(entry.getValue().generateUri())) continue;
                    commonDao.save(new Link(primeTerm, entry.getValue(), Link.ALIAS, Link.MORPHEME_WEIGHT));
                }
            }
        } catch (Exception e) {
            log.throwing(getClass().getName(), "findAliases", e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("{term}/{alias}")
    @Model
    public Link addAlias(@PathVariable String term, @PathVariable String alias) {
        Term primTerm = commonDao.get(Term.class, "name", term);
        if (primTerm == null) {
            primTerm = commonDao.save(new Term(term));
        }
        Term aliasTerm = commonDao.get(Term.class, "name", alias);
        if (aliasTerm == null) {
            aliasTerm = commonDao.save(new Term(alias));
        }
        return commonDao.save(new Link(primTerm, aliasTerm, Link.ALIAS));
    }

    public Term getPrime(Term term) {
        Link link = linkDao.getPrimeForAlias(term.getUri());
        if (link != null) {
            return (Term) link.getUid1();
        }
        return term;
    }
}
