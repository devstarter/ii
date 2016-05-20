package org.ayfaar.app.services.links;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.utils.exceptions.ExceptionCode;
import org.ayfaar.app.utils.exceptions.LogicalException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Service
public class LinkService {
    private LinkDao linkDao;

    @Inject
    public LinkService(LinkDao linkDao) {
        this.linkDao = linkDao;
    }

    public LinkProvider getByUris(String uri1, String uri2) {
        return findByUris(uri1, uri2).orElseThrow(() -> new LogicalException(ExceptionCode.LINK_NOT_FOUND, uri1, uri2));
    }

    public Optional<LinkProvider> findByUris(String uri1, String uri2) {
        final List<Link> links = linkDao.getByUris(uri1, uri2);
        if (links.size() > 1) throw new RuntimeException("Found more then one link for uri1: `"+uri1+"` and uri2: `"+uri2+"`");
        if (links.isEmpty()) return Optional.empty();
        final LinkProvider provider = new LinkProvider(links.get(0), linkDao::save);
        return Optional.of(provider);
    }
}
