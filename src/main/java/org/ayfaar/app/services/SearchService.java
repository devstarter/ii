package org.ayfaar.app.services;

import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SearchService {
    @Autowired AliasesMap aliasesMap;

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
