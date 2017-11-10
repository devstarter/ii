package org.ayfaar.app.exec;

import org.ayfaar.app.Application;
import org.ayfaar.app.dao.ItemDao;
import org.hibernate.criterion.MatchMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("remote")
@WebAppConfiguration
public class Clean6tom {

    @Autowired private ItemDao itemDao;

    @Test
    public void main() {

       itemDao.getLike("number", "6.", MatchMode.START).forEach((item -> {
            String content = item.getContent();

            content = content.replaceAll("\\?—\\?", " — ");

            item.setContent(content);
            item.setTaggedContent(null);
            itemDao.save(item);
       }));
    }
}
