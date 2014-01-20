package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.ayfaar.app.utils.TermUtils.isCosmicCode;

@Controller
@RequestMapping("api/search")
public class SearchController {
    @Autowired AliasesMap aliasesMap;
    @Autowired TermDao termDao;

    /*public Object search(String query) {
        ModelMap modelMap = new ModelMap();
        modelMap.put("terms", searchAsTerm(query));
        modelMap.put("items", searchInItems(query));
        return modelMap;
    }

    private List searchInItems(String query) {
        return null;
    }*/

    @RequestMapping("term")
    @Model
    private List<Term> searchAsTerm(@RequestParam String query) {
        List<Term> allTerms = aliasesMap.getAllTerms();
        List<Term> matches = new ArrayList<Term>();

        Pattern pattern = null;
        if (isCosmicCode(query)) {
            String regexp = "";
            for (int i=0; i< query.length(); i++) {
                if (i > 0 && query.charAt(i) == query.charAt(i-1)) {
                    continue;
                }
                regexp += "("+query.charAt(i)+")+";
            }
            pattern = Pattern.compile(regexp);
        }

        for (Term term : allTerms) {
            if (term.getName().equals(query)) {
                matches.add(0, term);
            } else if (term.getName().contains(query)) {
                matches.add(term);
            } else if (pattern != null && pattern.matcher(term.getName()).find()) {
                matches.add(term);
            }
        }

        return matches;
    }
}
