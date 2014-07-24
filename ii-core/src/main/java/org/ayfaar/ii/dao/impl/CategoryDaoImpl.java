package org.ayfaar.ii.dao.impl;

import org.ayfaar.ii.dao.CategoryDao;
import org.ayfaar.ii.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.*;

@Repository
public class CategoryDaoImpl extends AbstractHibernateDAO<Category> implements CategoryDao {

    public CategoryDaoImpl() {
        super(Category.class);
    }

    @Override
    public List<Category> getTopLevel() {
        return list(criteria().add(isNull("parent")));
    }

    @Override
    public Category getForItem(String itemUri) {
        return (Category) criteria()
                .add(le("start", itemUri))
                .add(ge("end", itemUri))
                .uniqueResult();
    }

//    @Override
//    public List<Category> getChildren(String uri) {
//        return list(criteria().add());
//    }
}
