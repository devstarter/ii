package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.RecordDao;
import org.ayfaar.app.model.Record;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

import static org.hibernate.criterion.Restrictions.like;

@Repository
public class RecordDaoImpl extends AbstractHibernateDAO<Record> implements RecordDao {

    public RecordDaoImpl() {
        super(Record.class);
    }

    @Override
    public List<Record> get(String nameOrCode, String year, Record.Kind kind, boolean isUrlPresent, Pageable pageable){

        Criteria criteria = criteria(pageable);

        if (nameOrCode != null && !nameOrCode.isEmpty())
            criteria.add(Restrictions.or(
                like("code", nameOrCode, MatchMode.ANYWHERE),
                like("name", nameOrCode, MatchMode.ANYWHERE)));

        if (year != null && !year.isEmpty()) criteria.add(like("code", year, MatchMode.ANYWHERE));
        if (isUrlPresent) criteria.add(Restrictions.isNotNull("audioUrl"));

        if (kind != null) {
            if (kind == Record.Kind.k) {
                criteria.add(like("code", "-k", MatchMode.END));
            } else {
                criteria.add(Restrictions.not(like("code", "-k", MatchMode.END)));
                criteria.add(Restrictions.not(like("code", "-m", MatchMode.END)));
            }
        }

        return criteria.list();
    }
}
