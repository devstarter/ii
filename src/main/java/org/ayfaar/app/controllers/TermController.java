package org.ayfaar.app.controllers;

import lombok.AllArgsConstructor;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.event.EventPublisher;
import org.ayfaar.app.event.TermAddEvent;
import org.ayfaar.app.model.*;
import org.ayfaar.app.services.EntityLoader;
import org.ayfaar.app.services.itemRange.ItemRangeService;
import org.ayfaar.app.services.links.LinkService;
import org.ayfaar.app.services.topics.TopicProvider;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Supplier;

import static org.ayfaar.app.model.LinkType.ABBREVIATION;
import static org.ayfaar.app.model.LinkType.ALIAS;
import static org.ayfaar.app.utils.ValueObjectUtils.convertToPlainObjects;
import static org.springframework.util.StringUtils.isEmpty;

@Controller
@RequestMapping("api/term")
public class TermController {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(TermController.class.getName());

    @Autowired CommonDao commonDao;
    @Autowired TermDao termDao;
    @Autowired LinkDao linkDao;
    @Autowired TermService termService;
    @Autowired LinkService linkService;
    @Autowired EntityLoader entityLoader;
    @Autowired TermServiceImpl aliasesMap;
    @Autowired SuggestionsController suggestionsController;
    @Inject TermsMarker termsMarker;
    @Inject EventPublisher publisher;
    @Inject NewSearchController searchController;
    @Inject ItemRangeService itemRangeService;
    @Inject TermsFinder termsFinder;
    @Inject TopicService topicService;


    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Term add(Term term) {
        return add(term.getName(), term.getShortDescription(), term.getDescription());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ModelMap get(@RequestParam("name") String termName, @RequestParam(required = false) boolean mark) {
        termName = termName.replace("_", " ");
        Optional<TermService.TermProvider> providerOpt = termService.getMainOrThis(termName);
        if (!providerOpt.isPresent()) {
            return null;
        }
        final TermService.TermProvider provider = providerOpt.get();

        ModelMap modelMap = new ModelMap();//(ModelMap) getModelMap(term);
        Term term = provider.getTerm();

        modelMap.put("uri", term.getUri());
        modelMap.put("name", term.getName());
        if (mark) {
            if (isEmpty(term.getTaggedShortDescription()) && !isEmpty(term.getShortDescription())) {
                term.setTaggedShortDescription(termsMarker.mark(term.getShortDescription()));
                termDao.save(term);
            }
            if (isEmpty(term.getTaggedDescription()) && !isEmpty(term.getDescription())) {
                term.setTaggedDescription(termsMarker.mark(term.getDescription()));
                termDao.save(term);
            }
            modelMap.put("shortDescription", term.getTaggedShortDescription());
            modelMap.put("description", term.getTaggedDescription());
        } else {
            modelMap.put("shortDescription", term.getShortDescription());
            modelMap.put("description", term.getDescription());
        }

        // LINKS

        List<Quote> quotes = new ArrayList<>();
        linkService.getAllLinksBetween(provider.getUri(), Item.class)
                .forEach(p -> quotes.add(getQuote(p.taggedQuote(), p.get(Item.class).get())));

        Set<UID> related = new LinkedHashSet<>();
        Set<UID> aliases = new LinkedHashSet<>();

        provider.getAbbreviations().forEach(p -> aliases.add(p.getTerm()));
        provider.getAliases().forEach(p -> aliases.add(p.getTerm()));
        UID code = provider.getCode().isPresent() ? provider.getCode().get().getTerm() : null;

        // Нужно также включить цитаты всех синонимов и сокращений и кода
        Set<UID> aliasesQuoteSources = new HashSet<UID>(aliases);
        if (code != null) {
            aliasesQuoteSources.add(code);
        }
        for (UID uid : aliasesQuoteSources) {
            linkService.getAllLinksFor(uid.getUri())
                    .forEach(p -> {
                        final UID source = entityLoader.get(p.not(uid.getUri()));
                        if (source instanceof Item) {
                            final Quote quote = getQuote(p.taggedQuote(), source.getUri());
                            if (quotes.stream().noneMatch(q -> Objects.equals(quote.quote, q.quote))) {
                                quotes.add(quote);
                            }
                        }
                        else if (ABBREVIATION.equals(p.type()) || ALIAS.equals(p.type())/* || CODE.equals(p.type())*/) {
                            aliases.add(source);
                        }
                        else {
                            related.add(source);
                        }
                    });
        }

        aliases.removeIf(item -> item.getUri().equals(term.getUri()));

        quotes.sort(Comparator.comparing(o -> o.uri));

        // mark quotes with strong
        List<String> allAliasesWithAllMorphs = provider.getAllAliasesWithAllMorphs();
        for (Quote quote : quotes) {
            String text = quote.quote;
            if (text == null || text.isEmpty() || text.contains("strong")) continue;
            quote.quote = StringUtils.markWithStrong(text, allAliasesWithAllMorphs);
        }

        Optional<TopicProvider> topicOpt = StreamEx.<Supplier<Optional<TopicProvider>>>of(
                    () -> topicService.findByName(term.getName()), // получаем топик по имени термина
                    () -> topicService.contains(term.getName()),
                    () -> topicService.getAllLinkedWith(term.getUri()).findFirst(), // по линку с термином
                    () -> allAliasesWithAllMorphs.stream().map(a -> topicService.findByName(a)).filter(Optional::isPresent).findFirst().orElseGet(Optional::empty), // по имени по всем алиасам
                    () -> allAliasesWithAllMorphs.stream().map(a -> topicService.getAllLinkedWith(UriGenerator.generate(Term.class, a)).findFirst()).filter(Optional::isPresent).findFirst().orElseGet(Optional::empty)) // по линками по всем алиасам

                .map(Supplier::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        modelMap.put("code", code);
        modelMap.put("quotes", quotes);
        modelMap.put("related", toPlainObjectWithoutContent(related));
        modelMap.put("aliases", toPlainObjectWithoutContent(aliases));
        modelMap.put("categories", searchController.inCategories(termName));
        topicOpt.ifPresent(topicProvider -> {
            modelMap.put("topicResources", topicProvider.resources());
        });

        return modelMap;
    }

    private Object toPlainObjectWithoutContent(Set<UID> related) {
        return convertToPlainObjects(related, (entity, map) -> map.remove("content"));
    }

    private ModelMap getQuote(Link link, UID source, boolean mark) {
        ModelMap map = new ModelMap();
        String quote = link.getQuote() != null ? link.getQuote() : ((Item) source).getContent();
        if (mark) {
            quote = link.getTaggedQuote() != null ? link.getTaggedQuote() : ((Item) source).getTaggedContent();
        }
        map.put("quote", quote);
        map.put("uri", source.getUri());
        return map;
    }

    private Quote getQuote(String taggedQuote, String itemUri) {
        if (isEmpty(taggedQuote)) {
            taggedQuote = entityLoader.<Item>get(itemUri).getTaggedContent();
        }
        return new Quote(taggedQuote, itemUri);
    }

    @AllArgsConstructor
    class Quote {
        public String quote;
        public String uri;
    }

    @RequestMapping("related")
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
        return convertToPlainObjects(related, (entity, map) -> map.remove("content"));
    }

//    @RequestMapping(value = "/", method = POST)
//    @Model
    public Term add(String name, String description) {
        return add(name, null, description);
    }

    public Term add(String name, String shortDescription, String description) {
        name = name.replace("\"", "").replace("«", "").replace("»", "").trim();
//        name = WordUtils.capitalize(name, new char[]{'@'}); // Делаем первую букву большой, @ - знак который не появляеться в названии, чтобы поднялась только первая буква всей фразы
        Term term = termDao.getByName(name);
        if (term == null) {
            term = termDao.save(new Term(name, shortDescription, description));
            if (shortDescription != null)
                term.setTaggedShortDescription(termsMarker.mark(shortDescription));
            if (description != null)
                term.setTaggedDescription(termsMarker.mark(description));
            String termName = term.getName();
            log.info("Added: " + termName);
            if (TermUtils.isComposite(termName)) {
                String target = TermUtils.getNonCosmicCodePart(termName);
                if (target != null) {
                    findAliases(term, target, termName.replace(target, ""));
                }
            } else if (!TermUtils.isCosmicCode(termName)) {
                findAliases(term, termName, "");
            }
//            publisher.publishEvent(new NewTermEvent(term));
        } else {
            String oldShortDescription = null;
            if (shortDescription != null && !shortDescription.isEmpty()) {
                oldShortDescription = term.getShortDescription();
                term.setShortDescription(shortDescription);
                term.setTaggedShortDescription(termsMarker.mark(shortDescription));
            }
            String oldDescription = null;
            if (description != null && !description.isEmpty()) {
                oldDescription = term.getDescription();
                term.setDescription(description);
                term.setTaggedDescription(termsMarker.mark(description));
            }
            termDao.save(term);
//            publisher.publishEvent(new TermUpdatedEvent(term, oldShortDescription, oldDescription));
        }

        publisher.publishEvent(new TermAddEvent(term.getName()));

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

                                log.info("Alias added: "+alias);
                            }
                            aliases.add(alias);
                        }
                    }
                }
               /* for (Map.Entry<String, Term> entry : aliases.entrySet()) {
                    if (primeTerm.uri().equals(entry.getValue().generateUri())) continue;
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
        return suggestionsController.suggestions(filter);
    }

    @RequestMapping(value = "get-short-description", produces = "text/plain; charset=utf-8")
    @ResponseBody
    public String getShortDescription(@RequestParam String name) {
        return termService.getTerm(name).getShortDescription();
    }

    @RequestMapping("remove/{name}")
    public void remove(@PathVariable String name) {
        termDao.remove(termDao.getByName(name).getUri());
    }


    @RequestMapping("reload-aliases-map")
    public void reloadAliasesMap() {
        termService.reload();
    }
}
