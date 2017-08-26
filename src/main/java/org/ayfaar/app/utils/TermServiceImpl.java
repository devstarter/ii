package org.ayfaar.app.utils;


import lombok.Data;
import lombok.Getter;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.event.NewLinkEvent;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.ayfaar.app.services.EntityLoader;
import org.ayfaar.app.services.links.LinkProvider;
import org.ayfaar.app.services.links.LinkService;
import org.ayfaar.app.services.moderation.Action;
import org.ayfaar.app.services.moderation.ModerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static org.ayfaar.app.model.LinkType.*;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;
import static org.springframework.util.StringUtils.isEmpty;

@Component
public class TermServiceImpl implements TermService {
    private static final Logger logger = LoggerFactory.getLogger(TermService.class);

    private final CommonDao commonDao;
    private EntityLoader entityLoader;
    private final LinkService linkService;
    private final ModerationService moderationService;
    private final TermsTaggingUpdater taggingUpdater;
    private final AsyncTaskExecutor taskExecutor;
    private final TermDao termDao;
    private final LinkDao linkDao;

    private Map<String, LinkInfo> links;
    private Map<String, TermProvider> aliasesMap;
    // names - provider map
    private ArrayList<Map.Entry<String, TermProvider>> sortedList;
    private List<TermDao.TermInfo> termsInfo;

    @Autowired
    public TermServiceImpl(LinkDao linkDao,
                           TermDao termDao,
                           CommonDao commonDao,
                           EntityLoader entityLoader,
                           LinkService linkService,
                           @Lazy ModerationService moderationService,
                           @Lazy TermsTaggingUpdater taggingUpdater,
                           @Lazy AsyncTaskExecutor taskExecutor) {
        this.linkDao = linkDao;
        this.termDao = termDao;
        this.commonDao = commonDao;
        this.entityLoader = entityLoader;
        this.linkService = linkService;
        this.moderationService = moderationService;
        this.taggingUpdater = taggingUpdater;
        this.taskExecutor = taskExecutor;
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
        sortedList = new ArrayList<>(aliasesMap.entrySet());
        sortedList.sort((o1, o2) -> Integer.compare(o2.getKey().length(), o1.getKey().length()));
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

        @Override
        public TermProvider rename(String newName) {
            if (getName().equals(newName)) return this;
                
            Assert.hasText(newName, "Invalid term name: " + newName);
            newName = newName.trim();
            moderationService.check(Action.TERM_RENAME, getName(), newName);

            logger.info("Term renaming started. Old name: `{}`, new name: `{}`", getName(), newName);
            final List<Link> allLinks = linkService.getAllLinksFor(uri)
                    .peek(link -> logger.trace(link.toString()))
                    .map(LinkProvider::id)
                    .map(linkDao::get)
                    .toList();

            final Term oldTerm = getTerm();
            try{commonDao.remove(oldTerm);}catch (Exception ignore) {}

            if (!getName().toLowerCase().equals(newName.toLowerCase())) {
                get(newName).ifPresent(term -> {
                    // if new term already exist, remove it
                    logger.info("Remove already existing term: " + term.getName());
                    try {commonDao.remove(term.getTerm());}catch (Exception ignore) {}
                });
            }

            final Term newTerm = new Term(newName);
            newTerm.setShortDescription(oldTerm.getShortDescription());
            newTerm.setDescription(oldTerm.getDescription());
            newTerm.setTaggedShortDescription(oldTerm.getTaggedShortDescription());
            newTerm.setTaggedDescription(oldTerm.getTaggedDescription());
            commonDao.save(newTerm);

            allLinks.forEach(link -> {
                link.setLinkId(null);
                if (link.getUid1().getUri().equals(oldTerm.getUri())) {
                    link.setUid1(newTerm);
                } else {
                    link.setUid2(newTerm);
                }
                linkDao.save(link);
            });

            linkService.reload();

            final long newLinksCount = linkService.getAllLinksFor(newTerm.getUri()).count();

            if (newLinksCount != allLinks.size()) {
                logger.warn("Something went wrong while renaming. New links dump:");
                linkService.getAllLinksFor(newTerm.getUri()).forEach(link -> logger.trace(link.toString()));
            }

            if (TermUtils.isComposite(newName)) {
                String target = TermUtils.getNonCosmicCodePart(newName);
                if (target != null) {
                    loadMorthems(newTerm, target, newName.replace(target, ""));
                }
            } else if (!TermUtils.isCosmicCode(newName)) {
                loadMorthems(newTerm, newName, "");
            }

            reload();
            logger.info("Term renaming finished. Old name: `{}`, new name: `{}`", getName(), newName);

            moderationService.notice(Action.TERM_RENAMED, getName(), newName);

            String finalNewName = newName;
            return get(newName).orElseThrow(() -> new RuntimeException("Term renaming procedure error, cannot find new term: "+ finalNewName));
        }

        @Override
        public void markAllContentAsync() {
            taskExecutor.submit(() -> taggingUpdater.update(getName()));
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

    @Override
    public void loadMorthems(Term primeTerm, String target, String prefix) {
        Morpher morpher;
        try {
            morpher = new Morpher(target);
            if (morpher.getData()) {
                Set<String> aliases = new HashSet<>();
                for (Morpher.Morph morph : morpher.getAllMorph()) {
                    if (morph == null) {
                        return;
                    }
                    String alias = prefix+morph.text;
                    if (!alias.isEmpty() && !alias.equals(primeTerm.getName())) {
                        if (!aliases.contains(alias)) {
                            TermMorph termMorph = commonDao.get(TermMorph.class, alias);
                            if (termMorph == null) {
                                commonDao.save(new TermMorph(alias, primeTerm.getUri()));

                                logger.info("Alias added: "+alias);
                            }
                            aliases.add(alias);
                        }
                    }
                }
               /* for (Map.Entry<String, Term> entry : aliases.entrySet()) {
                    if (primeTerm.uri().equals(entry.getValue().generateUri())) continue;
                    commonDao.save(new Link(primeTerm, entry.getValue(), Link.ALIAS, Link.MORPHEME_WEIGHT));
                }*/
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<TermProvider> findTerms(String text) {
        Set<TermProvider> contains = new HashSet<>();
        text = text.toLowerCase();

        for (Map.Entry<String, TermService.TermProvider> entry : getAll()) {
            String key = entry.getKey();
            Matcher matcher = compile("((" + RegExpUtils.W + ")|^)" + key
                    + "((" + RegExpUtils.W + ")|$)", Pattern.UNICODE_CHARACTER_CLASS)
                    .matcher(text);
            if (matcher.find()) {
                contains.add(entry.getValue());
                text = text.replaceAll(key, "");
            }
        }

        return contains;
    }
}
