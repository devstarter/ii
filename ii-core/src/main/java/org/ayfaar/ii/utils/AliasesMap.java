package org.ayfaar.ii.utils;

import org.ayfaar.ii.dao.CommonDao;
import org.ayfaar.ii.dao.LinkDao;
import org.ayfaar.ii.dao.TermDao;
import org.ayfaar.ii.model.Term;
import org.ayfaar.ii.model.TermMorph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.sort;
import static java.util.regex.Pattern.compile;

@Component
@Lazy
public class AliasesMap extends LinkedHashMap<String, AliasesMap.Proxy> {
    @Autowired TermDao termDao;
    @Autowired LinkDao linkDao;
    @Autowired CommonDao commonDao;

    private List<Term> allTerms;
    private List<TermMorph> allTermMorphs;
    private Map<String, Proxy> proxyMap;

    @PostConstruct
    private void load() {
        clear();
        allTerms = termDao.getAll();
        allTermMorphs = commonDao.getAll(TermMorph.class);

        proxyMap = new HashMap<String, Proxy>();
        Map<String, Proxy> tmpMap = new HashMap<String, Proxy>();

        for (Term term : allTerms) {
            Proxy proxy = proxyMap.get(term.getUri());
            if (proxy == null) {
                proxy = new Proxy(term);
                proxyMap.put(term.getUri(), proxy);
            }
            tmpMap.put(term.getName(), proxy);
        }
        for (TermMorph termMorph : allTermMorphs) {
            Proxy proxy = proxyMap.get(termMorph.getTermUri());
            if (proxy == null) {
                proxy = new Proxy(termMorph.getTermUri());
                proxyMap.put(termMorph.getTermUri(), proxy);
            }
            tmpMap.put(termMorph.getName(), proxy);
        }

        List<Map.Entry<String, Proxy>> entries =
                new ArrayList<Map.Entry<String, Proxy>>(tmpMap.entrySet());

        sort(entries, new Comparator<Map.Entry<String, Proxy>>() {
            @Override
            public int compare(Map.Entry<String, Proxy> o1, Map.Entry<String, Proxy> o2) {
                int i = new Integer(o2.getKey().length()).compareTo(o1.getKey().length());
                if (i == 0) {
                    i = o1.getKey().compareTo(o2.getKey());
                }
                return i;
            }
        });

        for (Map.Entry<String, Proxy> entry : entries) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void reload() {
        load();
    }

    public Proxy put(String alias, Term term) {
        return put(alias, new Proxy(term));
    }

    @Override
    public Proxy put(String key, Proxy value) {
        return super.put(key.toLowerCase(), value);
    }

    public Proxy get(String key) {
        return super.get(key.toLowerCase());
    }

    public class Proxy {
        private String uri;
        private Term term;

        public Proxy(Term term) {
            this.term = term;
            uri = term.getUri();
        }

        public Proxy(String uri) {
            this.uri = uri;
        }

        public Term getTerm() {
            if (term == null) {
                /*Link link = linkDao.getPrimeForAlias(term.getUri());
                if (link != null) {
                    prime = (Term) link.getUid1();
                } else {
                    prime = term;
                }*/
                term = termDao.get(uri);
            }
            return term;
        }

        public String getUri() {
            return uri;
        }
    }

    public List<Term> getAllTerms() {
        return allTerms;
    }

    public List<Term> findTermsInside(String content) {
        Set<Term> contains = new HashSet<Term>();
        content = content.toLowerCase();

        for (Map.Entry<String, AliasesMap.Proxy> entry : entrySet()) {
            String key = entry.getKey().toLowerCase();
            Matcher matcher = compile("((" + RegExpUtils.W + ")|^)" + key
                    + "((" + RegExpUtils.W + ")|$)", Pattern.UNICODE_CHARACTER_CLASS)
                    .matcher(content);
            if (matcher.find()) {
                contains.add(entry.getValue().getTerm());
                content = content.replaceAll(key, "");
            }
        }

        List<Term> sorted = new ArrayList<Term>(contains);
        sort(sorted, new Comparator<Term>() {
            @Override
            public int compare(Term o1, Term o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });
        return sorted;
    }
}
