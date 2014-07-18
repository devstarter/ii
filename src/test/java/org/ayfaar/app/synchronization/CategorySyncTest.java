package org.ayfaar.app.synchronization;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.synchronization.mediawiki.CategorySync;
import org.ayfaar.app.synchronization.mediawiki.MediaWikiBotHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
public class CategorySyncTest {

    @Autowired
    MediaWikiBotHelper botHelper;
    @Autowired
    CategorySync categorySync;
    @Autowired CategoryDao categoryDao;

    @Test
    public void testSynchronize() throws Exception {
        categorySync.synchronize(categoryDao.get("name", "Параграф 14.16.2.9"));
    }
}
