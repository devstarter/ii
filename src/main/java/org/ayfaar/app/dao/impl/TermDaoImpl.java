package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.springframework.stereotype.Repository;

import static org.hibernate.criterion.Restrictions.ilike;

@Repository
public class TermDaoImpl extends AbstractHibernateDAO<Term> implements TermDao {
    public TermDaoImpl() {
        super(Term.class);
    }

    @Override
    public Term getByName(String name) {
        return (Term) criteria()
                .add(ilike("name", name))
                .uniqueResult();
    }

}
