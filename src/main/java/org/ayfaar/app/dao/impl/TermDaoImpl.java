package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ilike;
import static org.hibernate.criterion.Restrictions.isNotNull;

@Repository
public class TermDaoImpl extends AbstractHibernateDAO<Term> implements TermDao {
    public TermDaoImpl() {
        super(Term.class);
    }

    @Override
    public Term getByName(@NotNull String name) {
        return (Term) criteria()
                .add(eq("name", name.trim()))
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

    @SuppressWarnings("unchecked")
    @Override
    public List<TermInfo> getAllTermInfo() {
        @SuppressWarnings("JpaQlInspection") List<Object[]> list = currentSession()
                .createQuery("select t.name, case when t.shortDescription is null then 0 else 1 end from Term t")
                .list();

        List<TermInfo> termsInfo = new ArrayList<TermInfo>();
        for (Object[] info : list) {
            termsInfo.add(new TermInfo((String) info[0], (Integer)info[1] == 1));
        }
        return termsInfo;
    }

    @Override
    public List<Term> getAllWithDescriptionGid() {
        return criteria()
                .add(isNotNull("descriptionGid"))
                .list();
    }
}
