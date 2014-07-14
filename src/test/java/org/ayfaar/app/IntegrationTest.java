package org.ayfaar.app;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
public class IntegrationTest {
    /*@Autowired
    ItemDao itemDao;

    @Test
    public void integrationTest() {
        Item item = itemDao.getByNumber("1.0001");
        assertNotNull(item);
    }*/
}
