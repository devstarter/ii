package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ilike;

@Repository
public class TermDaoImpl extends AbstractHibernateDAO<Term> implements TermDao {
    public TermDaoImpl() {
        super(Term.class);
    }

    @Override
    public Term getByName(String name) {
        return (Term) criteria()
                .add(eq("name", name))
                .uniqueResult();
    }

    @Override
    public List<Term> getLike(String field, String value) {
        return list(criteria()
                .add(ilike(field, value))
                .setMaxResults(20)
        );
    }

    @Override
    public List<Term> getGreaterThan(String field, Object value) {
        return criteria()
                .add(Restrictions.gt(field, value))
                .list();
    }

    @Override
    public List<Object[]> getAllTermInfo() {
        return criteria().setProjection(Projections.projectionList()
                .add(Projections.property("name"))
                .add(Projections.property("shortDescription")))
                .list();
    }
}
