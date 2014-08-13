package org.ayfaar.app.controllers;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.Math.min;
import static java.util.Arrays.asList;

@Controller
@RequestMapping("v2/suggestions")
public class SuggestionsController implements Comparator{
    @Autowired AliasesMap aliasesMap;

    public static final int MAX_SUGGESTIONS = 7;

    @RequestMapping("{q}")
    @ResponseBody

    public List<String> suggestions(@PathVariable String q) {
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
        Collections.sort(suggestions, new SuggestionsController());
        return suggestions;
    }

    public List<String> getSuggestedTerms(String query, List<String> suggestions) {
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

    @Override
    public int compare(Object o1, Object o2) {

        return Integer.valueOf(o1.toString().length()).compareTo(o2.toString().length());
    }
}
