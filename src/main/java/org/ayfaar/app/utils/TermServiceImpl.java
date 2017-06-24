package org.ayfaar.app.utils;


import lombok.Data;
import lombok.Getter;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.event.NewLinkEvent;
import org.ayfaar.app.model.*;
import org.ayfaar.app.services.EntityLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.sort;
import static java.util.regex.Pattern.compile;
import static org.ayfaar.app.model.LinkType.*;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class TermServiceImpl implements TermService {
    private static final Logger logger = LoggerFactory.getLogger(TermService.class);

    private final CommonDao commonDao;
    private EntityLoader entityLoader;
    private final TermDao termDao;
    private final LinkDao linkDao;

    private Map<String, LinkInfo> links;
    private Map<String, TermProvider> aliasesMap;
    // names - provider map
    private ArrayList<Map.Entry<String, TermProvider>> sortedList;
    private List<TermDao.TermInfo> termsInfo;

    @Autowired
    public TermServiceImpl(LinkDao linkDao, TermDao termDao, CommonDao commonDao, EntityLoader entityLoader) {
        this.linkDao = linkDao;
        this.termDao = termDao;
        this.commonDao = commonDao;
        this.entityLoader = entityLoader;
    }

    @PostConstruct
    public void load() {
		logger.info("Terms loading...");
        aliasesMap = new HashMap<>();
        List<TermMorph> allTermMorphs = commonDao.getAll(TermMorph.class);
        termsInfo = termDao.getAllTermInfo();
        List<Link> allSynonyms = linkDao.getAllSynonyms();

        links = new HashMap<>();
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
        sortedList = new ArrayList<Map.Entry<String, TermService.TermProvider>>(aliasesMap.entrySet());
        sort(sortedList, new Comparator<Map.Entry<String, TermService.TermProvider>>() {
            @Override
            public int compare(Map.Entry<String, TermService.TermProvider> o1, Map.Entry<String, TermService.TermProvider> o2) {
                return Integer.compare(o2.getKey().length(), o1.getKey().length());
            }
        });
		logger.info("Terms loading finish");
    }

    public void reload() {
        load();
    }

    @Override
    public void save(Term term) {
        commonDao.save(term);
        load();
    }

    @Data
    private class LinkInfo {
        private LinkType type;
        private Term mainTerm;

        private LinkInfo(LinkType type, Term term) {
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
//            return termDao.get(uri);
            return entityLoader.get(uri);
        }

        public List<TermProvider> getAliases() {
            return getListProviders(ALIAS, getName());
        }

        public List<TermProvider> getAbbreviations() {
            return getListProviders(ABBREVIATION, getName());
        }

        public Optional<TermProvider> getCode() {
            List<TermProvider> codes = getListProviders(CODE, getName());
            return codes.size() > 0 ? Optional.of(codes.get(0)) : Optional.empty();
        }

        public Optional<TermProvider> getMain() {
            return Optional.ofNullable(hasMain() ? aliasesMap.get(getValueFromUri(Term.class, mainTermUri).toLowerCase()) : null);
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

        public LinkType getType() {
            return links.get(uri) != null ? links.get(uri).getType() : null;
        }

        public boolean hasMain() {
            return mainTermUri != null;
        }

        public boolean isAbbreviation() {
            return ABBREVIATION.equals(getType());
        }

        public boolean isAlias() {
            return ALIAS.equals(getType());
        }

        public boolean isCode() {
            return CODE.equals(getType());
        }

        @Override
        public boolean hasCode() {
            return !getListProviders(CODE, getName()).isEmpty();
        }

        @Override
        public List<String> getAllAliasesWithAllMorphs() {
            List<String> list = getMorphs();
            List<String> aliasesSearchQueries = getAllMorphs(getAllAliases());
            list.addAll(aliasesSearchQueries);
            return list;
        }

        public List<String> getAllAliasesAndAbbreviationsWithAllMorphs() {
            final List<String> list = getAllAliasesWithAllMorphs();
            list.addAll(getAllMorphs(getAbbreviations()));
            return list;
        }

        @Override
        public Optional<String> getShortDescription() {
            return hasShortDescription ? Optional.of(getTerm().getShortDescription()) : Optional.empty();
        }

        List<String> getAllMorphs(List<TermProvider> providers) {
            List<String> morphs = new ArrayList<String>();

            for(TermProvider provider : providers) {
                morphs.addAll(provider.getMorphs());
            }
            return morphs;
        }

        List<TermProvider> getAllAliases() {
            List<TermProvider> aliases = new ArrayList<TermProvider>();
            TermProvider code = getCode().isPresent() ? getCode().get() : null;

            aliases.addAll(getAliases());
            aliases.addAll(getAbbreviations());
            if(code != null) {
                aliases.add(getCode().get());
            }
            return aliases;
        }
    }


    @Override
    public Optional<TermProvider> get(String name) {
        return Optional.ofNullable(aliasesMap.get(name.toLowerCase()));
    }

    @Override
    public Optional<TermProvider> getByUri(String uri) {
        return Optional.ofNullable(aliasesMap.get(getValueFromUri(Term.class, uri)));
    }

    @Override
    public Optional<TermProvider> getMainOrThis(String name) {
        final Optional<TermProvider> providerOpt = get(name);
        if (providerOpt.isPresent() && providerOpt.get().getMain().isPresent()) {
            return providerOpt.get().getMain();
        }
        return providerOpt;
    }

    @Override
    public List<Map.Entry<String, TermProvider>> getAll() {
        return sortedList;
    }

    @Override
    public List<TermDao.TermInfo> getAllInfoTerms() {
        return termsInfo;
    }

    @Override
    public Term getTerm(String name) {
        TermProvider termProvider = aliasesMap.get(name.toLowerCase());
        return termProvider != null ? termProvider.getTerm() : null;
    }

    private List<TermProvider> getListProviders(LinkType type, String name) {
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
        sorted.sort(Comparator.comparing(o -> o.getName().toLowerCase()));
        return sorted;
    }

    @EventListener
    private void onNewLink(NewLinkEvent event) {
        final Link link = event.link;
        if (link.getUid1() instanceof Term && link.getUid2() instanceof Term) {
            onTermsLinked((Term) link.getUid1(), (Term) link.getUid2(), link.getType());
        }
    }

    private void onTermsLinked(Term mainTerm, Term aliasTerm, LinkType linkType) {
        if (linkType == LinkType.ALIAS) {
            boolean mainTermChanged = false;
            if (isEmpty(mainTerm.getShortDescription())) {
                mainTerm.setShortDescription(aliasTerm.getShortDescription());
                mainTermChanged = true;
            }
            if (isEmpty(mainTerm.getDescription())) {
                mainTerm.setDescription(aliasTerm.getDescription());
                mainTermChanged = true;
            }
            if (mainTermChanged) termDao.save(mainTerm);
        }
        reload();
    }
}
