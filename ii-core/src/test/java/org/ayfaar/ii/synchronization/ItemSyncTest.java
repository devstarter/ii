package org.ayfaar.ii.synchronization;

import org.ayfaar.ii.SpringTestConfiguration;
import org.ayfaar.ii.dao.ItemDao;
import org.ayfaar.ii.synchronization.mediawiki.ItemSync;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
@Ignore
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
