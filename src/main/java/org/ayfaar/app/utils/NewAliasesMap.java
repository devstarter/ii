package org.ayfaar.app.utils;


import lombok.Data;
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

@Component
public class NewAliasesMap  {
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private TermDao termDao;
    @Autowired
    private LinkDao linkDao;

    private Map<String, LinkInfo> links;
    public Map<String, TermProvider> aliasesMap;

    @PostConstruct
    private void load() {
        aliasesMap = new HashMap<String, TermProvider>();

        List<TermMorph> allTermMorphs = commonDao.getAll(TermMorph.class);
        List<TermInfo> termsInfo = termDao.getAllTermInfo();
        List<Link> allSynonyms = linkDao.getAllSynonyms();


        links = new HashMap<String, LinkInfo>();
        for(Link link : allSynonyms) {
            links.put(link.getUid2().getUri(), new LinkInfo(link.getType(), (Term)link.getUid1()));
        }

        for(TermInfo info : termsInfo) {
            String uri = UriGenerator.generate(Term.class, info.getName());
            String link = null;

            if(links.containsKey(uri)) {
                link = links.get(uri).getMainTerm().getUri();
            }
            aliasesMap.put(info.getName(), new TermProvider(uri, link, info.isHasShortDescription()));
        }

        for(TermMorph morph : allTermMorphs) {
            String link = null;
            if(links.containsKey(morph.getTermUri())) {
                link = links.get(morph.getTermUri()).getMainTerm().getUri();
            }
            aliasesMap.put(morph.getName(), new TermProvider(morph.getTermUri(), link, false));
        }
    }

    @Data
    public class TermInfo {
        private String name;
        private boolean hasShortDescription;

        public TermInfo(String name, boolean hasShortDescription) {
            this.name = name;
            this.hasShortDescription = hasShortDescription;
        }
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

    @Data
    public class TermProvider {
        private String uri;
        private String mainTermUri;
        private boolean hasShortDescription;

        public TermProvider(String uri, String mainTermUri, boolean hasShortDescription) {
            this.uri = uri;
            this.mainTermUri = mainTermUri;
            this.hasShortDescription = hasShortDescription;
        }

        public Term getTerm() {
            return termDao.get(uri);
        }
    }

    public TermProvider getTermProvider(String name) {
        return aliasesMap.get(name);
    }

    public Set<Map.Entry<String, TermProvider>> getAll() {
        return aliasesMap.entrySet();
    }

    public Term getTerm(String name) {
        TermProvider termProvider = aliasesMap.get(name);
        return termProvider != null ? termProvider.getTerm() : null;
    }

    public TermProvider getMainTermProvider(String name) {
        TermProvider provider = getTermProvider(name);
        String uriToMainTerm = provider.getMainTermUri();
        return uriToMainTerm != null ? getTermProvider(UriGenerator.getValueFromUri(Term.class, uriToMainTerm)) : null;
    }

    public byte getTermType(String name) {
        return links.get(UriGenerator.generate(Term.class, name)).getType();
    }
}
