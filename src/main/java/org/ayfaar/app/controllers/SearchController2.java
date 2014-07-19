package org.ayfaar.app.controllers;

import org.ayfaar.app.controllers.search.Suggestion;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

// todo: сделать его контроллером
// todo: указать url: v2/search
@Controller
@RequestMapping("v2/search")
public class SearchController2 {
    @Autowired
    private TermDao termDao;
    @Autowired
    AliasesMap aliasesMap;

    private int suggestionsCount = 7;


    public List<Suggestion> suggestions(String q) {
        String query1 = q + "%";
        String query2 = "[%" + " " + q + "%]|[" + "%-" + q + "%";
        String query4 = "%" + q + "%";

        List<Term> terms = getTerms(q, query1);
        List<Term> termsFromDB;
        if(terms.size() < suggestionsCount) {
            termsFromDB = getTerms(q, query2);
            terms = addSuggestions(terms, termsFromDB);
            if(terms.size() < suggestionsCount) {
                termsFromDB.clear();
                termsFromDB = getTerms(q, query4);
                terms = addSuggestions(terms, termsFromDB);
            }
        }

        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        for(Term t : terms) {
            Suggestion suggestion = new Suggestion();
            suggestion.setLabel(t.getName());
            suggestions.add(suggestion);
        }
        return suggestions;
    }

    private List<Term> getTerms(String q, String query) {
        List<Term> terms = termDao.getLike("name", query);
        return terms;
    }

    private List<Term> addSuggestions(List<Term> terms, List<Term> termsFromDB) {
        int index = 0;
        while(terms.size() < suggestionsCount && (index < termsFromDB.size())) {
            Term term = termsFromDB.get(index);
            if(!terms.contains(term)) {
                terms.add(term);
            }
            index++;
        }
        return terms;
    }
}
