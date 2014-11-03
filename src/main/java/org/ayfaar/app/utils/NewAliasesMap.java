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
public class NewAliasesMap extends LinkedHashMap<String, NewAliasesMap.TermProvider> {
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private TermDao termDao;
    @Autowired
    private LinkDao linkDao;

    public Map<String, TermProvider> aliasesMap;

    @PostConstruct
    private void load() {
        aliasesMap = new HashMap<String, TermProvider>();

        List<TermMorph> allTermMorphs = commonDao.getAll(TermMorph.class);
        List<TermInfo> termsInfo = getTermInfo();
        List<Link> links = new ArrayList<Link>();

        for(TermInfo info : termsInfo) {
            links.addAll(linkDao.getAliases(UriGenerator.generate(Term.class, info.getName())));
        }

        for(TermInfo info : termsInfo) {
            String uri = UriGenerator.generate(Term.class, info.getName());
            TermProvider provider = new TermProvider(uri, getUriToMainTerm(links, uri), info.isHasShortDescription());
            aliasesMap.put(info.getName(), provider);
        }

        for(TermMorph morph : allTermMorphs) {
            TermProvider provider = new TermProvider(morph.getTermUri(),
                    getUriToMainTerm(links, morph.getTermUri()), false);
            aliasesMap.put(morph.getName(), provider);
        }
    }

    private List<TermInfo> getTermInfo() {
        List<TermInfo> termsInfo = new ArrayList<TermInfo>();
        List<Object[]> allTermInfo = termDao.getAllTermInfo();

        for(int i = 0; i < allTermInfo.size(); i++) {
            Object[] info = allTermInfo.get(i);
            boolean hasShortDescription = info[1] != null ? true : false;
            termsInfo.add(new TermInfo((String)info[0], hasShortDescription));
        }
        return termsInfo;
    }

    private String getUriToMainTerm(List<Link> links, String uri) {
        String mainUri = null;
        for(Link link : links) {
            if(link.getUid2().getUri().equals(uri)) {
                mainUri = link.getUid1().getUri();
            }
        }
        return mainUri;
    }

    @Data
    private class TermInfo {
        private String name;
        private boolean hasShortDescription;

        public TermInfo(String name, boolean hasShortDescription) {
            this.name = name;
            this.hasShortDescription = hasShortDescription;
        }
    }

    public class TermProvider {
        private String uri;
        private String mainTermUri;
        private boolean hasShortDescription;

        public TermProvider(String uri, String mainTermUri, boolean hasShortDescription) {
            this.uri = uri;
            this.mainTermUri = mainTermUri;
            this.hasShortDescription = hasShortDescription;
        }
    }
}
