package org.ayfaar.app.services.itemRange;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemsRangeDao;
import org.ayfaar.app.model.ItemsRange;
import org.ayfaar.app.model.TermParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
public class ItemRangeServiceImpl implements ItemRangeService{

    @Autowired CommonDao commonDao;
    @Autowired ItemsRangeDao itemsRangeDao;

    private List<TermParagraph> allTermParagraph;
    private List<ItemsRange> itemsRanges;



    @PostConstruct
    private void init() {
        log.info("TermParagraph loading...");

        allTermParagraph = commonDao.getAll(TermParagraph.class);

        log.info("TermParagraph loaded");

        log.info("ItemsRanges loading...");

        itemsRanges = itemsRangeDao.getWithCategories();

        log.info("ItemsRanges loaded");

    }

    public void reload() {
        init();
    }

    @Override
    public List<ItemsRange> getWithCategories(){
       return itemsRanges;
    }

    @Override
    public StreamEx<String> getParagraphsByTerm(String term){
        return StreamEx.of(allTermParagraph)
                .filter(t -> t.getTerm().equals(term))
                .map(TermParagraph::getParagraph)
                .sorted();
    }
}
