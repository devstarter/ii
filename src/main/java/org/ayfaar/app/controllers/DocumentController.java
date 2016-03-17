package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Document;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("api/document")
public class DocumentController {
    @Inject CommonDao commonDao;

    @RequestMapping(method = RequestMethod.POST)
    public Document test(@RequestParam String name,
                         @RequestParam(required = false) String pdf,
                         @RequestParam(required = false) String author,
                         @RequestParam(required = false) String annotation) {
        return commonDao.save(new Document(name, pdf, annotation, author));
    }
}
