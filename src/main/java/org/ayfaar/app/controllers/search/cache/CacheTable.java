package org.ayfaar.app.controllers.search.cache;


import org.ayfaar.app.dao.CommonDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.util.List;

@Controller
@RequestMapping("api/cache")
public class CacheTable {

    @Inject CommonDao commonDao;


    @RequestMapping("clear")
    public  void clearCacheTable(){

    List<CacheEntity> cacheEntities = commonDao.getAll(CacheEntity.class);
        for (CacheEntity cacheEntity : cacheEntities) {
            commonDao.remove(cacheEntity);
        }
    }

    @RequestMapping("clearby/{uri}")
    public  void clearByURI(@PathVariable String uri){

        commonDao.remove(CacheEntity.class,uri);
    }

}
