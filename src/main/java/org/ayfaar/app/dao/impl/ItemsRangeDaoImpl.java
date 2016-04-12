package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.ItemsRangeDao;
import org.ayfaar.app.model.ItemsRange;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemsRangeDaoImpl extends AbstractHibernateDAO<ItemsRange> implements ItemsRangeDao {
    public ItemsRangeDaoImpl() {
        super(ItemsRange.class);
    }

    @Override
    public List<ItemsRange> getWithCategories() {
        return criteria()
                .add(Restrictions.isNotNull("category"))
                .list();
    }
}
