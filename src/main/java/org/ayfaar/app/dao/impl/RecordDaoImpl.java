package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RecordDaoImpl extends AbstractHibernateDAO<Record> implements RecordDao {

    public RecordDaoImpl() {
        super(Record.class);
    }

    @Override
    public List<Record> get(String nameOrCode, String year, boolean isUrlPresent){

        Criteria criteria = criteria();
        if (nameOrCode!=null)
            criteria.add(Restrictions.or(
                Restrictions.like("code", nameOrCode, MatchMode.ANYWHERE),
                Restrictions.like("name", nameOrCode, MatchMode.ANYWHERE)));
        if (year!=null) criteria.add(Restrictions.like("code", year, MatchMode.ANYWHERE));
        if (isUrlPresent) criteria.add(Restrictions.isNotNull("audioUrl"));
                //.setMaxResults(9);
        return criteria.list();
    }
}
