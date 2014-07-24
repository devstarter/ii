package org.ayfaar.ii.synchronization;

import org.ayfaar.ii.SpringTestConfiguration;
import org.ayfaar.ii.dao.TermDao;
import org.ayfaar.ii.synchronization.mediawiki.ItemSync;
import org.ayfaar.ii.synchronization.mediawiki.MediaWikiBotHelper;
import org.ayfaar.ii.synchronization.mediawiki.SyncUtils;
import org.ayfaar.ii.synchronization.mediawiki.TermSync;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
@Ignore
public class TermSyncTest {

    @Autowired
    MediaWikiBotHelper botHelper;
    @Autowired TermDao termDao;
    @Autowired
    TermSync termSync;
    @Autowired
    ItemSync itemSync;
    @Autowired
    SyncUtils syncUtils;

    @Test
    public void testSynchronize() throws Exception {
        termSync.synchronize(termDao.getByName("Время"));
    }

    @Test
    public void test2() throws Exception {
        termSync.synchronize(termDao.getByName("УПДИ"));
        itemSync.syncScheduled();
        termSync.syncScheduled();
        termSync.syncScheduled();
//        syncUtils.syncAllScheduled();
        botHelper.push();
    }
}
