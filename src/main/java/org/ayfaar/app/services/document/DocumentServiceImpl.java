package org.ayfaar.app.services.document;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Collectors;

@Component()
public class DocumentServiceImpl implements DocumentService {

    @Autowired CommonDao commonDao;
    @Override
    public Map<String, String> getAllUriNames(){
        return commonDao.getAll(Document.class).stream().collect(Collectors.toMap(document -> document.getUri(),document -> document.getName()));
    }
}
