package org.ayfaar.app.utils;


import lombok.Data;
import lombok.Getter;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.sort;
import static java.util.regex.Pattern.compile;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class TermsMapImpl implements TermsMap {
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private TermDao termDao;
    @Autowired
    private LinkDao linkDao;

    private Map<String, LinkInfo> links;
    private Map<String, TermProvider> aliasesMap;
    private ArrayList<Map.Entry<String, TermProvider>> sortedList;

    @PostConstruct
    public void load() {
        aliasesMap = new HashMap<String, TermProvider>();

        List<TermMorph> allTermMorphs = commonDao.getAll(TermMorph.class);
        List<TermDao.TermInfo> termsInfo = termDao.getAllTermInfo();
        List<Link> allSynonyms = linkDao.getAllSynonyms();

        links = new HashMap<String, LinkInfo>();
        for(Link link : allSynonyms) {
            links.put(link.getUid2().getUri(), new LinkInfo(link.getType(), (Term)link.getUid1()));
        }

        for(TermDao.TermInfo info : termsInfo) {
            String uri = UriGenerator.generate(Term.class, info.getName());
            String mainTermUri = null;

            if(links.containsKey(uri)) {
                mainTermUri = links.get(uri).getMainTerm().getUri();
            }
            aliasesMap.put(info.getName().toLowerCase(), new TermProviderImpl(uri, mainTermUri, info.isHasShortDescription()));
        }

        for(TermMorph morph : allTermMorphs) {
            final TermProvider termProvider = aliasesMap.get(getValueFromUri(Term.class, morph.getTermUri()).toLowerCase());
            aliasesMap.put(morph.getName().toLowerCase(), termProvider);
        }

        // prepare sorted List by term name length, longest terms first
        sortedList = new ArrayList<Map.Entry<String, TermsMap.TermProvider>>(aliasesMap.entrySet());
        sort(sortedList, new Comparator<Map.Entry<String, TermsMap.TermProvider>>() {
            @Override
            public int compare(Map.Entry<String, TermsMap.TermProvider> o1, Map.Entry<String, TermsMap.TermProvider> o2) {
                return Integer.compare(o2.getKey().length(), o1.getKey().length());
            }
        });
    }

    public void reload() {
        load();
    }

    @Data
    private class LinkInfo {
        private byte type;
        private Term mainTerm;

        private LinkInfo(byte type, Term term) {
            this.type = type;
            this.mainTerm = term;
        }
    }

    public class TermProviderImpl implements TermProvider {
        @Getter
        private String uri;
        private String mainTermUri;
        private boolean hasShortDescription;

        public TermProviderImpl(String uri, String mainTermUri, boolean hasShortDescription) {
            this.uri = uri;
            this.mainTermUri = mainTermUri;
            this.hasShortDescription = hasShortDescription;
        }

        public String getName() {
            return getValueFromUri(Term.class, uri);
        }

        public boolean hasShortDescription() {
            return hasShortDescription;
        }

        public Term getTerm() {
            return termDao.get(uri);
        }

        public List<TermProvider> getAliases() {
            return getListProviders(Link.ALIAS, getName());
        }

        public List<TermProvider> getAbbreviations() {
            return getListProviders(Link.ABBREVIATION, getName());
        }

        public TermProvider getCode() {
            List<TermProvider> codes = getListProviders(Link.CODE, getName());
            return codes.size() > 0 ? codes.get(0) : null;
        }

        public TermProvider getMainTermProvider() {
            return hasMainTerm() ? aliasesMap.get(getValueFromUri(Term.class, mainTermUri).toLowerCase()) : null;
        }

        public List<String> getMorphs() {
            List<String> morphs = new ArrayList<String>();
            for (Map.Entry<String, TermProvider> map : aliasesMap.entrySet()) {
                if(map.getValue().getUri().equals(getUri())) {
                    morphs.add(map.getKey());
                }
            }
            return morphs;
        }

        public Byte getType() {
            return links.get(uri) != null ? links.get(uri).getType() : null;
        }

        public boolean hasMainTerm() {
            return mainTermUri != null;
        }

        public boolean isAbbreviation() {
            return Link.ABBREVIATION.equals(getType());
        }

        public boolean isAlias() {
            return Link.ALIAS.equals(getType());
        }

        public boolean isCode() {
            return Link.CODE.equals(getType());
        }
    }

    @Override
    public TermProvider getTermProvider(String name) {
        return aliasesMap.get(name.toLowerCase());
    }

    @Override
    public List<Map.Entry<String, TermProvider>> getAll() {
        return sortedList;
    }

    @Override
    public Term getTerm(String name) {
        TermProvider termProvider = aliasesMap.get(name.toLowerCase());
        return termProvider != null ? termProvider.getTerm() : null;
    }

    private List<TermProvider> getListProviders(byte type, String name) {
        List<TermProvider> providers = new ArrayList<TermProvider>();

        for(Map.Entry<String, LinkInfo> link : links.entrySet()) {
            if(link.getValue().getType() == type && link.getValue().getMainTerm().getName().equals(name)) {
                providers.add(aliasesMap.get(getValueFromUri(Term.class, link.getKey().toLowerCase())));
            }
        }
        return providers;
    }

    @Deprecated // for old version and mediawiki sync support
    public List<Term> findTermsInside(String content) {
        Set<Term> contains = new HashSet<Term>();
        content = content.toLowerCase();

        for (Map.Entry<String, TermProvider> entry : sortedList) {
            String key = entry.getKey();
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
