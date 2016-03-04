package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.*;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.ayfaar.app.events.SearchQuoteEvent;
import org.ayfaar.app.utils.Content;
import org.ayfaar.app.utils.EmailNotifier;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.TermsMarker;
import org.hibernate.criterion.MatchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.*;
import static org.ayfaar.app.utils.RegExpUtils.w;
import static org.ayfaar.app.utils.TermUtils.isCosmicCode;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Controller
@RequestMapping("api/search")
public class SearchController {
    @Autowired TermService termService;
    @Autowired TermDao termDao;
    @Autowired ItemDao itemDao;
    @Autowired ArticleDao articleDao;
    @Autowired LinkDao linkDao;
    @Autowired CommonDao commonDao;
    @Autowired TermMorphDao termMorphDao;
    @Autowired EmailNotifier notifier;
    @Autowired ApplicationEventPublisher eventPublisher;
    @Autowired TermsMarker termsMarker;
    @Autowired NewSearchController searchController;

    private Map<String, List<ModelMap>> searchInContentCatch = new HashMap<String, List<ModelMap>>();

    /*public Object search(String query) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("terms", searchAsTerm(query));
        modelMap.put("items", searchInItems(query));
        return modelMap;
    }

    private List searchInItems(String query) {
        return null;
    }*/

    @RequestMapping("content")
    @ResponseBody
    private List<ModelMap> searchInContent(@RequestParam String query,
                                           @RequestParam(required = false, defaultValue = "0") Integer page) {
        final Integer pageSize = 20;
        query = query.toLowerCase();
        query = query.trim();
        String catchKey = query+"#$%^&"+page;
        List<ModelMap> catchedResult = searchInContentCatch.get(catchKey);
        if (catchedResult != null) {
            return catchedResult;
        }

        List<Content> items;
        List<ModelMap> modelMaps = new ArrayList<ModelMap>();

        if (query.indexOf("!") == 0) {
            // поиск только введённого запроса
            query = query.replaceFirst("!", "");
            items = commonDao.findInAllContent(query, page*pageSize, pageSize);
        } else {

            TermService.TermProvider provider = termService.getTermProvider(query);

            Term term = null;
            if (provider != null) {
                term = provider.getTerm();
            } else {
                for (Map.Entry<String, TermService.TermProvider> entry : termService.getAll()) {
                    if (entry.getKey().equals(query)) {
                        term = entry.getValue().getTerm();
                        break;
                    }
                }
            }

            List<String> aliasesList = new ArrayList<String>();

            if (term != null) {
                Term primeTerm = (Term) linkDao.getPrimeForAlias(term.getUri());
                if (primeTerm == null) {
                    primeTerm = term;
                }
                List<Link> links = linkDao.getAliases(primeTerm.getUri());
                for (Link link : links) {
                    Term aliasTerm = (Term) link.getUid2();
                    aliasesList.add(aliasTerm.getName());
                    for (TermMorph morph : termMorphDao.getList("termUri", aliasTerm.getUri())) {
                        aliasesList.add(morph.getName());
                    }
                }
                for (TermMorph morph : termMorphDao.getList("termUri", primeTerm.getUri())) {
                    aliasesList.add(morph.getName());
                }
            }


            // get all cases of the word
            // test case
            aliasesList.addAll(termMorphDao.getAllMorphs(query));

            if (!aliasesList.isEmpty()) {
                String newQuery = "";
                for (String alias : aliasesList) {
                    newQuery += (alias.toLowerCase() + "|");
                }
                newQuery = newQuery.substring(0, newQuery.length() - 1);
                query = newQuery;
            }

            query = query.replaceAll("\\*", w + "*");
            if (aliasesList.size() > 0) {
                items = commonDao.findInAllContent(aliasesList, page * pageSize, pageSize);
            } else {
                items = commonDao.findInAllContent(query, page * pageSize, pageSize);
            }
        }
        // [^\.\?!]* - star is greedy, so pattern will find the last match to make first group as long as possible
        Pattern pattern = Pattern.compile("([^\\.\\?!]*)\\b(" + query + ")\\b([^\\.\\?!]*)([\\.\\?!]*)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);


        for (Content item : items) {
            ModelMap map = new ModelMap();
            map.put("uri", item.getUri());
            map.put("name", item.getName());
            Matcher matcher = pattern.matcher(item.getContent());
            if (matcher.find()) {
                // (?iu) - insensitive, unicode, \b - a word boundary, $1 - first group
                String quote = matcher.group().replaceAll("(?iu)\\b(" + query + ")\\b", "<strong>$1</strong>");
                map.put("quote", quote.trim());
                modelMaps.add(map);
            }
        }
        searchInContentCatch.put(catchKey, modelMaps);
        return modelMaps;
    }

    @RequestMapping("term")
    @ResponseBody
    private ModelMap searchAsTerm(@RequestParam String query) {
        query = query.trim();
        List<Map.Entry<String, TermService.TermProvider>> allProviders = termService.getAll();
        List<String> matches = new ArrayList<String>();
        Term exactMatchTerm = null;

        Pattern pattern = null;
        if (isCosmicCode(query)) {
            String regexp = "";
            for (int i=0; i< query.length(); i++) {
                if (i > 0 && query.charAt(i) == query.charAt(i-1)) {
                    continue;
                }
                regexp += "("+query.charAt(i)+")+";
            }
            pattern = Pattern.compile(regexp.toLowerCase());
        }

        for (Map.Entry<String, TermService.TermProvider> providers : allProviders) {
            if (providers.getKey().equals(query.toLowerCase())) {
                exactMatchTerm = providers.getValue().getTerm();
            } else if (providers.getKey().contains(query.toLowerCase())
                    || pattern != null && pattern.matcher(providers.getKey()).find()) {
                matches.add(providers.getKey());
            }
        }

        TermMorph morph = termMorphDao.getByName(query);
        if (morph != null) {
            exactMatchTerm = termService.getTerm(getValueFromUri(Term.class, morph.getTermUri()));
        }

        List<Term> terms = new ArrayList<Term>();
        for (String match : matches) {
            Term prime = termService.getTerm(match);
            boolean has = false;
            for (Term term : terms) {
                if (term.getUri().equals(prime.getUri())) {
                    has = true;
                }
            }

            if (!has) terms.add(prime);
        }

        for (Map.Entry<String, TermService.TermProvider> entry : termService.getAll()) {
            String word = entry.getKey();
            pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|])|^)(" + word
                    + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
            Matcher contentMatcher = pattern.matcher(query);
            if (contentMatcher.find()) {
                terms.add(entry.getValue().getTerm());
            }
        }
        Collections.reverse(terms);

        ModelMap modelMap = new ModelMap();
        modelMap.put("terms", terms);
        modelMap.put("articles", articleDao.getLike("name", query, MatchMode.ANYWHERE));
        modelMap.put("exactMatchTerm", exactMatchTerm);
        modelMap.put("categories", searchController.inCategories(query));

        return modelMap;
    }

    @RequestMapping(value = "rate/{kind}", method = RequestMethod.POST)
    public void rate(@PathVariable String kind,
                     @RequestParam String uri,
                     @RequestParam String query,
                     @RequestParam(required = false) String quote) {
        if (kind.equals("+")) {
            Term term = termService.getTerm(query);
            Item item = itemDao.get(uri);
            boolean possibleDuplication = false;
            Link link = null;
            if (term != null && item != null) {
                final List<Link> links = linkDao.get(term, item);
                if (links.size() == 0) {
                    link = new Link(term, item, quote, termsMarker.mark(quote));
                    link.setSource("search");
                    linkDao.save(link);
                } else {
                    possibleDuplication = true;
                }
            }
            //notifier.rate(term, item, quote, link != null ? link.getLinkId() : null);
            eventPublisher.publishEvent(new SearchQuoteEvent(term, item, quote, link != null ? link.getLinkId() : null));
        }
    }
}
