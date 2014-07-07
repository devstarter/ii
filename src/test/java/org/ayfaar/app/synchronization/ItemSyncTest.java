package org.ayfaar.app.synchronization;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.importing.SpringConfiguration;
import org.ayfaar.app.synchronization.mediawiki.ItemSync;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class ItemSyncTest {

    @Autowired
    ItemDao itemDao;
    @Autowired
    ItemSync itemSync;

    @Test
    public void testSynchronize() throws Exception {
        itemSync.synchronize(itemDao.getByNumber("14.16668"));
    }
}
