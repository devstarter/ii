package org.ayfaar.app.utils;


import lombok.Data;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Primary
public class NewAliasesMap implements TermsMap {
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
        List<TermDao.TermInfo> termsInfo = termDao.getAllTermInfo();
        List<Link> allSynonyms = linkDao.getAllSynonyms();

        links = new HashMap<String, LinkInfo>();
        for(Link link : allSynonyms) {
            links.put(link.getUid2().getUri(), new LinkInfo(link.getType(), (Term)link.getUid1()));
        }

        for(TermDao.TermInfo info : termsInfo) {
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
    private class LinkInfo {
        private byte type;
        private Term mainTerm;

        private LinkInfo(byte type, Term term) {
            this.type = type;
            this.mainTerm = term;
        }
    }

    @Override
    public TermProvider getTermProvider(String name) {
        return aliasesMap.get(name);
    }

    @Override
    public Set<Map.Entry<String, Term>> getAll() {
        return null;
    }

    @Override
    public Set<Map.Entry<String, TermProvider>> getAllProviders() {
        return aliasesMap.entrySet();
    }

    @Override
    public Term getTerm(String name) {
        TermProvider termProvider = aliasesMap.get(name);
        return termProvider != null ? termDao.get(termProvider.getUri()) : null;
    }

    @Override
    public TermProvider getMainTermProvider(String name) {
        TermProvider provider = getTermProvider(name);

        String uriToMainTerm = provider.getMainTermUri();
        return uriToMainTerm != null ? getTermProvider(UriGenerator.getValueFromUri(Term.class, uriToMainTerm)) : null;
    }

    @Override
    public byte getTermType(String name) {
        return links.get(UriGenerator.generate(Term.class, name)).getType();
    }

    public List<TermProvider> getAliases(String uri) {
        return getListProviders((byte)1, UriGenerator.getValueFromUri(Term.class, uri));
    }

    public List<TermProvider> getAbbreviations(String uri) {
        return getListProviders((byte) 2, UriGenerator.getValueFromUri(Term.class, uri));
    }

    public List<TermProvider> getCodes(String uri) {
        return getListProviders((byte) 4, UriGenerator.getValueFromUri(Term.class, uri));
    }

    private List<TermProvider> getListProviders(byte type, String name) {
        List<TermProvider> providers = new ArrayList<TermProvider>();

        for(Map.Entry<String, LinkInfo> link : links.entrySet()) {
            if(link.getValue().getType() == type && link.getValue().getMainTerm().getName().equals(name)) {
                providers.add(aliasesMap.get(UriGenerator.getValueFromUri(Term.class, link.getKey())));
            }
        }
        return providers;
    }
}
