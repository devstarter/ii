package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheUpdater {
    @Autowired
    private NewSearchController searchController;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private TermsMap termsMap;

    @Scheduled(cron="0 0 3 * * ?")
    public void update() {
        termsMap.reload();

        List<CacheEntity> cache = commonDao.getAll(CacheEntity.class);

        //clean cache for search result

        for (CacheEntity c : cache) {
            if (c.getUri().startsWith(UriGenerator.generate(Term.class, ""))) {
                update(c.getUri());
            }
        }
    }

    public void update(String uri) {
        searchController.search(UriGenerator.getValueFromUri(Term.class, uri), 0, null);
    }
}
