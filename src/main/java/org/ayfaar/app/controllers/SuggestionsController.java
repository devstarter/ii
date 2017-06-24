package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

@Controller
@RequestMapping("api/suggestions")
public class SuggestionsController {
    @Autowired TermService termService;

    private List<String> escapeChars = Arrays.asList("(", ")", "[", "]", "{", "}");
    public static final int MAX_SUGGESTIONS = 7;

    /**
     * the syntax is {variable_name:regular_expression}
     * variable named q, which value will be matched using regex .+
     */
    @RequestMapping("{q:.+}")
    @ResponseBody
    public List<String> suggestions(@PathVariable String q) {
        q = q.replace("*", ".*");
        q = q.replaceAll("\\s+", ".*");
        q = escapeRegexp(q);
        q = addDuplications(q);
        Queue<String> queriesQueue = new LinkedList<String>(asList(
                "^"+q,
                "[\\s\\-]" + q,
                q
        ));
        List<String> suggestions = new ArrayList<String>();

        while (suggestions.size() < MAX_SUGGESTIONS && queriesQueue.peek() != null) {
            List<String> founded = getSuggestedTerms(queriesQueue.poll(), suggestions);
            suggestions.addAll(founded.subList(0, min(MAX_SUGGESTIONS - suggestions.size(), founded.size())));
        }
        suggestions.sort((o1, o2) -> Integer.valueOf(o1.length()).compareTo(o2.length()));

        return suggestions;
    }

    protected static String addDuplications(String q) {
        return q.replaceAll("([A-Za-zА-Яа-яЁё])", "$1+-*$1*");
    }

    public List<String> getSuggestedTerms(String query, List<String> suggestions) {
        List<String> terms = new ArrayList<String>();
        Pattern pattern = Pattern.compile(query,CASE_INSENSITIVE + UNICODE_CASE);

        for (Map.Entry<String, TermService.TermProvider> entry : termService.getAll()) {
            Matcher matcher = pattern.matcher(entry.getValue().getName());
            String providerName = entry.getValue().getName();
            if(matcher.find() && !suggestions.contains(providerName) && !terms.contains(providerName)) {
                terms.add(entry.getValue().getName());
            }
        }
        Collections.reverse(terms);

        return terms;
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
