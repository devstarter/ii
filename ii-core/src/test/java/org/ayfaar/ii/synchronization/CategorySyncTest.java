package org.ayfaar.ii.synchronization;

import org.ayfaar.ii.SpringTestConfiguration;
import org.ayfaar.ii.dao.CategoryDao;
import org.ayfaar.ii.synchronization.mediawiki.CategorySync;
import org.ayfaar.ii.synchronization.mediawiki.MediaWikiBotHelper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
@Ignore
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
