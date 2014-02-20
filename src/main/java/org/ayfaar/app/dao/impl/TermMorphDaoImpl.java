package org.ayfaar.app.dao.impl;


import org.ayfaar.app.dao.TermMorphDao;
import org.ayfaar.app.model.TermMorph;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.ilike;

@Repository
public class TermMorphDaoImpl extends AbstractHibernateDAO<TermMorph> implements TermMorphDao {
    public TermMorphDaoImpl() {
        super(TermMorph.class);
    }

    @Override
    public TermMorph getByName(String name) {
        return (TermMorph) criteria()
                .add(ilike("name", name))
                .uniqueResult();
    }

    @Override
    public List<String> getAllMorphs(String termMorph) {
        String query = "SELECT name FROM term_morph WHERE LOWER(name) LIKE '"
                + termMorph.toLowerCase()
                + "' OR LOWER(term_uri) LIKE 'ии:термин:"
                + termMorph.toLowerCase()
                + "'";

        return sessionFactory.getCurrentSession().createSQLQuery(query).list();
    }
}
