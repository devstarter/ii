package org.ayfaar.app;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DaoTest extends IntegrationTest {
    @Inject LinkDao linkDao;
    @Inject CommonDao commonDao;

    @Test
    public void testGet() {
        Link link = linkDao.get("uid2.uri", "ии:пункт:1.0133");
        assertNotNull(link);
        assertNotNull(link.getUid1());
        assertNotNull(link.getUid2());
        link = commonDao.get(Link.class, "uid2.uri", "ии:пункт:1.0133");
        assertNotNull(link);
    }

    @Test
    public void testGetList() {
        List<Link> links = linkDao.getList("uid1.uri", "ии:термин:Время");
        assertFalse(links.isEmpty());
    }
}
