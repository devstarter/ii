package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.TermRecordDao;
import org.ayfaar.app.model.TermRecordFrequency;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TermRecordDaoImpl extends AbstractHibernateDAO<TermRecordFrequency> implements TermRecordDao {
    public TermRecordDaoImpl() {
        super(TermRecordFrequency.class);
    }

    @Override
    public List<TermRecordFrequency> getAllTermRecords(int countTerms){

        String query = "SELECT record, term, frequency FROM (" +
                "    SELECT" +
                "        record, term, frequency, " +
                "        @currcount \\:= IF(@currvalue = record, @currcount + 1, 1) AS rank," +
                "        @currvalue \\:= record AS whatever" +
                "    FROM term_record_frequency" +
                "    ORDER BY record, frequency DESC" +
                ") AS whatever WHERE rank <=                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                " + countTerms+"";

        List list = currentSession().createSQLQuery(query).setResultTransformer(Transformers.aliasToBean(TermRecordFrequency.class)).list();

        return list;
    }
}
