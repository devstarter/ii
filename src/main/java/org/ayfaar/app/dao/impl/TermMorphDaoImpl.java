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
        List<String> result;
        String query, nominative;

        // handling nominative case
        nominative = termMorph;
        query = "SELECT name FROM term_morph WHERE LOWER(term_uri) LIKE 'ии:термин:"
                + nominative.toLowerCase()
                + "'";
        result = sessionFactory.getCurrentSession().createSQLQuery(query).list();
        if(!result.isEmpty()){
            result.add(nominative);
            return result;
        }

        // handling another cases
        query = "SELECT term_uri FROM term_morph WHERE LOWER(name) LIKE '"
                + termMorph.toLowerCase()
                + "'";
        result = sessionFactory.getCurrentSession().createSQLQuery(query).list();
        if(result.isEmpty()){
            return result;
        }
        nominative = result.get(0).replaceFirst("ии:термин:","");
        query = "SELECT name FROM term_morph WHERE term_uri LIKE '"
                + result.get(0).toString()
                + "'";
        result = sessionFactory.getCurrentSession().createSQLQuery(query).list();
        result.add(nominative);
        return result;
    }
}
