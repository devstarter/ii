package org.ayfaar.app.services;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SearchService {
    @Autowired AliasesMap aliasesMap;

    public List<String> getTerms(String query) {
        List<String> terms = new ArrayList<String>();

        for (Term term : aliasesMap.getAllTerms()) {
            //todo: протестировать regexp'ом и вернуть удовлетворяющий критерию
            throw new NotImplementedException("issue19");
        }

        return terms;
    }
}
