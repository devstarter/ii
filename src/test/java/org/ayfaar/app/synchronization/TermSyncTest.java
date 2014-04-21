package org.ayfaar.app.synchronization;

import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.importing.SpringConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class TermSyncTest {

    @Autowired TermDao termDao;
    @Autowired TermSync termSync;

    @Test
    public void testSynchronize() throws Exception {
        termSync.synchronize(termDao.getByName("Время"));
    }
}
