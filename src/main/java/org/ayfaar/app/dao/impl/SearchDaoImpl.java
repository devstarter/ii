package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.SearchDao;
import org.ayfaar.app.model.Item;
import org.springframework.stereotype.Repository;

@Repository
public class SearchDaoImpl extends AbstractHibernateDAO<Item>  implements SearchDao {
    public SearchDaoImpl(Class<Item> entityClass) {
        super(entityClass);
    }
}
