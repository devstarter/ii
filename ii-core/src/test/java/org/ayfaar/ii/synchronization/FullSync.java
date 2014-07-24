package org.ayfaar.ii.synchronization;

import org.ayfaar.ii.SpringTestConfiguration;
import org.ayfaar.ii.dao.CategoryDao;
import org.ayfaar.ii.dao.ItemDao;
import org.ayfaar.ii.dao.TermDao;
import org.ayfaar.ii.model.Category;
import org.ayfaar.ii.model.Item;
import org.ayfaar.ii.model.Term;
import org.ayfaar.ii.synchronization.mediawiki.*;
import org.hibernate.criterion.MatchMode;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
@Ignore
public class FullSync {

    @Autowired
    MediaWikiBotHelper botHelper;
    @Autowired TermDao termDao;
    @Autowired ItemDao itemDao;
    @Autowired
    TermSync termSync;
    @Autowired CategoryDao categoryDao;
    @Autowired
    CategorySync categorySync;
    @Autowired
    ItemSync itemSync;
    @Autowired
    TOCSync tocSync;

    @Test
    public void dump() throws Exception {
//        for (Category category : categoryDao.getAll()) {
//            categorySync.synchronize(category);
//        }
        tocSync.synchronize();
        categorySync.syncScheduled();
        itemSync.syncScheduled();
        termSync.syncScheduled();
        botHelper.push();
    }

    @Test
    public void dump2() throws Exception {
        for (Category category : categoryDao.getLike("name", Category.PARAGRAPH_NAME, MatchMode.START)) {
            categorySync.synchronize(category);
        }
        botHelper.push();
    }

    @Test
    public void dump3() throws Exception {
        Item item = itemDao.getByNumber("10.10001");
        while(!item.getNumber().equals("10.10025")) {
            itemSync.synchronize(item);
            item = itemDao.get(item.getNext());
        }
        termSync.syncScheduled();
        botHelper.push();
    }

    @Test
    public void push() throws Exception {
        botHelper.push();
    }

    @Test
    public void terms() throws Exception {
        for (Term term : termDao.getGreaterThan("name", "ТТТ-УУЛЛКУУРРУ-СС-СТ")) {
            termSync.synchronize(term);
        }
        botHelper.push();
    }

    @Test
    public void items() throws Exception {
        for (Item item : itemDao.getLike("number", "10.", MatchMode.START)) {
            itemSync.synchronize(item);
        }
        botHelper.push();
    }
}
