package org.ayfaar.app.controllers.search.cache;


import org.ayfaar.app.dao.CommonDao;

import javax.inject.Inject;
import java.util.List;

public class CacheTable {

    @Inject CommonDao commonDao;

    public  void clearCacheTable(){

    List<CacheEntity> cacheEntities = commonDao.getAll(CacheEntity.class);
        for (CacheEntity cacheEntity : cacheEntities) {
            commonDao.remove(cacheEntity);
        }
    }

    public  void clearByURI(String uri){

        commonDao.remove(CacheEntity.class,uri);
    }

}
