package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.UIDDao;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class UIDDaoImpl implements UIDDao{
    @Autowired
    SessionFactory sessionFactory;

    public List<String> getAll() {
        return sessionFactory.getCurrentSession()
                .createSQLQuery("SELECT uri FROM uid").list();
    }

    @SuppressWarnings("JpaQlInspection")
    @Transactional( readOnly = false)
    public void removeByUri(String uri) {
        Query query = sessionFactory.getCurrentSession()
                .createQuery("delete from UID u where u.uri = :uri");
        query.setString("uri", uri);
        query.executeUpdate();
    }
}
