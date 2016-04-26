package org.ayfaar.app.controllers;

import one.util.streamex.StreamEx;
import org.ayfaar.app.services.document.DocumentService;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.services.videoResource.VideoResourceService;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

@Controller
@RequestMapping("api/suggestions")
public class SuggestionsController {

    @Autowired TermService termService;
    @Autowired TopicService topicService;
    @Autowired ContentsService contentsService;
    @Autowired DocumentService documentService;
    @Autowired VideoResourceService videoResourceService;

    private List<String> escapeChars = Arrays.asList("(", ")", "[", "]", "{", "}");
    List<String> suggestions;
    public static final int MAX_SUGGESTIONS = 5;

    /**
     * the syntax is {variable_name:regular_expression}
     * variable named q, which value will be matched using regex .+
     */
    @RequestMapping(value = "{q:.+}")
    @ResponseBody
    public List<String> suggestions(@PathVariable String q) {

        Queue<String> queriesQueue = getQueue(q);

        List<String> allSuggestions = new ArrayList<>();
        allSuggestions.addAll(getSuggestions(queriesQueue, "term"));
        return allSuggestions;
    }

    @RequestMapping(value = "all/{q:.+}")
    @ResponseBody
    public List<String> suggestions(@RequestParam String q,
                                    @RequestParam(required = false, defaultValue = "true") boolean with_terms,
                                    @RequestParam(required = false) boolean with_topic,
                                    @RequestParam(required = false) boolean with_category,
                                    @RequestParam(required = false) boolean with_doc,
                                    @RequestParam(required = false) boolean with_video
    ) {

        List<String> allSuggestions = new ArrayList<>();
        List<String> items = new ArrayList<>();
        if(with_topic)items.add("topic");
        if(with_category)items.add("category");
        if(with_doc)items.add("document");
        if(with_video)items.add("video");
        if(with_terms)items.add("term"); //default
        for (String item : items) {
            Queue<String> queriesQueue = getQueue(q);
            allSuggestions.addAll(getSuggestions(queriesQueue, item));
        }
        return allSuggestions;
    }

    private Queue<String> getQueue(String q){
        q = q.replace("*", ".*");
        q = q.replaceAll("\\s+", ".*");
        q = escapeRegexp(q);
        q = addDuplications(q);
        Queue<String> queriesQueue = new LinkedList<String>(asList(
                "^"+q,
                "[\\s\\-]" + q,
                q
        ));
        return queriesQueue;
    }
    private List<String> getSuggestions(Queue<String> queriesQueue, String item ) {
        suggestions = new ArrayList<String>();

        while (suggestions.size() < MAX_SUGGESTIONS && queriesQueue.peek() != null) {

            List<String> founded = null;
            List<String> names = null;

            if (item == "term") names = termService.getAll().stream()
                    .map(terms -> terms.getValue()
                            .getName()).collect(Collectors.toList());
            else if(item == "topic") names = topicService.getAllNames();
            else if(item == "category") names = contentsService.getAllCategories()
                    .map(o -> o.extractCategoryName()).collect(Collectors.toList());
            else if(item == "document") names = documentService.getAllNames();
            else if(item == "video") names = videoResourceService.getAll();

            founded = getSuggestedItems(queriesQueue.poll(), suggestions, names);

            suggestions.addAll(founded.subList(0, min(MAX_SUGGESTIONS - suggestions.size(), founded.size())));
        }
        Collections.sort(suggestions, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1.length()).compareTo(o2.length());
            }
        });

        return suggestions;
    }


    protected static String addDuplications(String q) {
        return q.replaceAll("([A-Za-zА-Яа-яЁё])", "$1+-*$1*");
    }

    private List<String> getSuggestedItems(String query, List<String> suggestions, List<String> names) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(query,CASE_INSENSITIVE + UNICODE_CASE);
        for (String name : names) {
            Matcher matcher = pattern.matcher(name);
            if(matcher.find() && !suggestions.contains(name) && !list.contains(name)) {
                list.add(name);
            }
        }
        Collections.reverse(list);

        return list;
    }

    private String escapeRegexp(String query) {
        for(String bracket : escapeChars) {
            if(query.contains(bracket)) {
                query = query.replace(bracket, "\\" + bracket);
            }
        }
        return query;
    }
}
