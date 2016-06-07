package org.ayfaar.app.dao;


import org.ayfaar.app.model.Record;
import java.util.List;

public interface RecordDao extends BasicCrudDao<Record>{

    List<Record> get(String nameOrCode, String year, boolean isUrlPresent);
}
