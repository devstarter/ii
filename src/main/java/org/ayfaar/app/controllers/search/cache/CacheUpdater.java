package org.ayfaar.app.controllers.search.cache;

import org.ayfaar.app.controllers.CategoryController;
import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.dao.impl.TermDaoImpl;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.TermsMarker;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheUpdater {
    @Autowired
    private NewSearchController searchController;
    @Autowired
    private CategoryController categoryController;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private TermsMap termsMap;
    @Autowired
    private TermsMarker termsMarker;

    @Scheduled(cron="0 0 3 * * ?")
    public void update() {
        termsMap.reload();

        updateCacheSearchResult();
        updateItemContent();
    }

    private void updateCacheSearchResult() {
        //clean cache for search results

        String uri = UriGenerator.generate(Term.class, "");
        List<CacheEntity> searchResultCache = commonDao.getLike(CacheEntity.class, "uri", (uri + "%"), Integer.MAX_VALUE);

        for (CacheEntity searchResult : searchResultCache) {
            searchController.search(UriGenerator.getValueFromUri(Term.class, searchResult.getUri()), 0, null);
        }
    }

    void updateItemContent() {
        List<Item> items = itemDao.getAll();
        for(Item item : items) {
            String markedContent = termsMarker.mark(item.getContent());
            if(!item.getContent().equals(markedContent)) {              //если добавлены новые термины
                item.setContent(markedContent);
                itemDao.save(item);
            }
        }
    }

    public void update(String uri) {
        //clean cache by uri

        if(uri.startsWith(UriGenerator.generate(Term.class, ""))) {
            searchController.search(UriGenerator.getValueFromUri(Term.class, uri), 0, null);
        } else if(uri.startsWith(UriGenerator.generate(Category.class, ""))) {
            categoryController.getContents(UriGenerator.getValueFromUri(Category.class, uri));
        }
    }
}
