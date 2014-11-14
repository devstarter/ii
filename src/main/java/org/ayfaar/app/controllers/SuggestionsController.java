package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.TermsMap;
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
import static java.util.regex.Pattern.*;

@Controller
@RequestMapping("api/suggestions")
public class SuggestionsController{
    @Autowired TermsMap termsMap;

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
        Collections.sort(suggestions, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1.length()).compareTo(o2.length());
            }
        });
        return suggestions;
    }

    public List<String> getSuggestedTerms(String query, List<String> suggestions) {
        List<String> terms = new ArrayList<String>();
        Pattern pattern = Pattern.compile(query,CASE_INSENSITIVE + UNICODE_CASE);

        for (Map.Entry<String, TermsMap.TermProvider> map: termsMap.getAll()) {
            Matcher matcher = pattern.matcher(map.getValue().getName());
            String providerName = map.getValue().getName();
            if(matcher.find() && !suggestions.contains(providerName) && !terms.contains(providerName)) {
                terms.add(map.getValue().getName());
            }
        }
        return terms;
    }
}
