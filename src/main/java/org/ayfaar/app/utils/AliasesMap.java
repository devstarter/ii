package org.ayfaar.app.utils;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.sort;

@Component
public class AliasesMap extends LinkedHashMap<String, AliasesMap.Proxy> {
    @Autowired TermDao termDao;
    @Autowired LinkDao linkDao;

    @PostConstruct
    private void load() {
        List<Term> terms = termDao.getAll();

        sort(terms, new Comparator<Term>() {
            @Override
            public int compare(Term o1, Term o2) {
                return new Integer(o2.getName().length()).compareTo(o1.getName().length());
            }
        });
        for (Term term : terms) {
            put(term.getName(), new Proxy(term));
        }
    }

    public class Proxy {
        private Term term;
        private Term prime;

        public Proxy(Term term) {
            this.term = term;
        }

        public Term getPrime() {
            if (prime == null) {
                Link link = linkDao.getPrimeForAlias(term.getUri());
                if (link != null) {
                    prime = (Term) link.getUid1();
                } else {
                    prime = term;
                }
            }
            return prime;
        }
    }
}
