package org.ayfaar.app.dao;


import org.ayfaar.app.model.Record;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecordDao extends BasicCrudDao<Record>{

    List<Record> get(String nameOrCode, String year, Record.Kind kind, boolean isUrlPresent, Pageable pageable);
}
