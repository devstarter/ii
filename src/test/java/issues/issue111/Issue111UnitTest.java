package issues.issue111;

import org.ayfaar.app.controllers.search.cache.CacheEntity;
import org.ayfaar.app.controllers.search.cache.CacheTable;
import org.ayfaar.app.controllers.search.cache.DBCache;
import org.ayfaar.app.dao.CommonDao;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;

public class Issue111UnitTest {

    @Inject
    DBCache dbCache;
    @Inject
    CommonDao commonDao;

    @Test
    public void ClearDBCache(){
        dbCache.clearAll();
        Assert.assertEquals(dbCache, new DBCache());
    }

    public void ClearCachTable(){
        CacheTable cacheTable = new CacheTable();
        cacheTable.clearCacheTable();
        Assert.assertEquals(commonDao.getAll(CacheEntity.class),0);
    }
    
}
