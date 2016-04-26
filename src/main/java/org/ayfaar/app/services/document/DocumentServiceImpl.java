package org.ayfaar.app.services.document;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component("documentService")
public class DocumentServiceImpl implements DocumentService {

    @Autowired CommonDao commonDao;
    @Override
    public List<String> getAllNames() {
        return commonDao.getAll(Document.class).stream().map(document -> document.getName()).collect(Collectors.toList());
    }
}
