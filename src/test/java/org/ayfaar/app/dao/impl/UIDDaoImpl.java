package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.UIDDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.model.UIDTest;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UIDDaoImpl extends AbstractHibernateDAO<UIDTest> implements UIDDao{
    public UIDDaoImpl() {
        super(UIDTest.class);
    }

    public List<UIDTest> getAll() {
        String query = "SELECT uri FROM uid";
        return sessionFactory.getCurrentSession().createSQLQuery(query).list();
    }

    @Transactional( readOnly = false)
    public void removeByUri(String uri) {
        Query query = sessionFactory.getCurrentSession().createQuery("delete from UID u where u.uri = :uri");
        query.setString("uri", uri);
        query.executeUpdate();
    }
}
