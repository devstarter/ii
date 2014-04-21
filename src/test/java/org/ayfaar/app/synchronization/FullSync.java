package org.ayfaar.app.synchronization;

import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.importing.SpringConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class FullSync {

    @Autowired TermDao termDao;
    @Autowired TermSync termSync;
    @Autowired CategoryDao categoryDao;
    @Autowired CategorySync categorySync;
    @Autowired ItemSync itemSync;
    @Autowired TOCSync tocSync;

    @Test
    public void dump() throws Exception {
//        for (Category category : categoryDao.getAll()) {
//            categorySync.synchronize(category);
//        }
        tocSync.synchronize();
        categorySync.syncScheduled();
        itemSync.syncScheduled();
        termSync.syncScheduled();
    }
}
