package org.ayfaar.app.dao;


import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.TermRecordFrequency;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TermRecordDao extends BasicCrudDao<TermRecordFrequency>{

    List<TermRecordFrequency> getAllTermRecords(int countTerms);
}
