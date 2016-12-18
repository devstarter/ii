package org.ayfaar.app.services.links;

import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.HasUri;
import org.ayfaar.app.model.LightLink;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.services.EntityLoader;
import org.ayfaar.app.utils.SoftCache;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LinkService {
    private LinkDao linkDao;
    private CommonDao commonDao;
    private EntityLoader entityLoader;
    private List<LightLink> allLinks;
    private SoftCache<LightLink, LinkProvider> cache = new SoftCache<>();

    @Inject
    public LinkService(LinkDao linkDao, CommonDao commonDao, EntityLoader entityLoader) {
        this.linkDao = linkDao;
        this.commonDao = commonDao;
        this.entityLoader = entityLoader;
    }

  /*  public LinkProvider getByUris(String uri1, String uri2) {
        return findByUris(uri1, uri2).orElseThrow(() -> new LogicalException(ExceptionCode.LINK_NOT_FOUND, uri1, uri2));
    }*/

    @PostConstruct
    protected void init() {
        allLinks = commonDao.getAll(LightLink.class);
    }
    /*
    public Optional<LinkProvider> findByUris(String uri1, String uri2) {
        final List<Link> links = linkDao.getByUris(uri1, uri2);
        if (links.size() > 1) throw new RuntimeException("Found more then one link for uri1: `"+uri1+"` and uri2: `"+uri2+"`");
        if (links.isEmpty()) return Optional.empty();
        final LinkProvider provider = new LinkProvider(links.get(0), this::linkSaver);
        return Optional.of(provider);
    }*/

    public StreamEx<? extends LinkProvider> getAllLinksBetween(HasUri uriOwner, Class<?> entityClass) {
        return getAllLinksBetween(uriOwner.getUri(), entityClass);
    }

    public StreamEx<? extends LinkProvider> getAllLinksBetween(String uri, Class<?> entityClass) {
        return getAllLinksFor(uri)
                .filter(linkProvider -> linkProvider.has(entityClass));
    }

    private Link linkSaver(LightLink link) {
        final Link entity = linkDao.get(link.getLinkId());
        entity.setComment(link.getComment());
        entity.setQuote(link.getQuote());
        entity.setRate(link.getRate());
        entity.setSource(link.getSource());
        entity.setTaggedQuote(link.getTaggedQuote());
        return linkDao.save(entity);
    }

    public StreamEx<? extends LinkProvider> getAllLinksFor(String uri) {
        return StreamEx.of(allLinks)
                .filter(link -> Objects.equals(link.getUid1(), uri) || Objects.equals(link.getUid2(), uri))
                .map(this::getLinkProvider);
    }

    public Optional<LinkProvider> getByUris(String uri1, String uri2) {
        return StreamEx.of(allLinks)
                .filter(link -> (Objects.equals(link.getUid1(), uri1) && Objects.equals(link.getUid2(), uri2))
                        || (Objects.equals(link.getUid1(), uri2) && Objects.equals(link.getUid2(), uri1)))
                .map(this::getLinkProvider)
                .findFirst();
    }

    private LinkProvider getLinkProvider(LightLink link) {
        return cache.getOrCreate(link, () -> new LinkProvider(link, this::linkSaver));
    }

    public void registerNew(Link link) {
        allLinks.add(LightLink.fromLink(link));
    }

    public void reload() {
        init();
    }

    public Optional<? extends LinkProvider> getLinkBetween(HasUri hasUri, Class<?> entityClass) {
        return getAllLinksFor(hasUri.getUri())
                .filter(linkProvider -> linkProvider.has(entityClass))
                .findFirst();
    }

    public Optional<String> getLinked(HasUri hasUri, Class<? extends UID> entityClass) {
        return getAllLinksFor(hasUri.getUri())
                .filter(linkProvider -> linkProvider.has(entityClass))
                .findFirst()
                .get()
                .get(entityClass);
    }
}
