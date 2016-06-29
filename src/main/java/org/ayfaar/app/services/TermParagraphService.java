package org.ayfaar.app.services;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.TermParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component()
public class TermParagraphService {

    @Autowired
    CommonDao commonDao;

    List<TermParagraph> allTermParagraph;
    @PostConstruct
    private void init() {
        log.info("TermParagraph loading...");

        allTermParagraph = commonDao.getAll(TermParagraph.class);

        log.info("TermParagraph loaded");
    }

    public List<String> getParagraphsByTerm(String term){
        return allTermParagraph.stream().filter(t ->
                t.getTerm().equals(term)).map(TermParagraph::getParagraph).collect(Collectors.toList());
    }
}
