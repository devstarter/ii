package org.ayfaar.app.controllers;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;
import static java.util.Arrays.asList;

@Controller
@RequestMapping("v2/search")
public class SearchController2 {
    @Autowired AliasesMap aliasesMap;

    public static final int MAX_SUGGESTIONS = 7;

    public List<String> suggestions(String q) {
        Queue<String> queriesQueue = new LinkedList<String>(asList(
                "^"+q,
                "[\\s\\-]" + q,
                q
        ));

        List<String> suggestions = new ArrayList<String>();

        while (suggestions.size() < MAX_SUGGESTIONS && queriesQueue.peek() != null) {
            List<String> founded = getTerms(queriesQueue.poll(), suggestions);
            suggestions.addAll(founded.subList(0, min(MAX_SUGGESTIONS - suggestions.size(), founded.size())));
        }
        return suggestions;
    }

    public List<String> getTerms(String query, List<String> suggestions) {
        List<String> terms = new ArrayList<String>();
        Pattern pattern = Pattern.compile(query);

        for (Term term : aliasesMap.getAllTerms()) {
            Matcher matcher = pattern.matcher(term.getName().toLowerCase());
            if(matcher.find() && !suggestions.contains(term.getName())) {
                terms.add(term.getName());
            }
        }
        return terms;
    }
}
