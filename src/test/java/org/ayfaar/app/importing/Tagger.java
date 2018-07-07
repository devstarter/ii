package org.ayfaar.app.importing;

import org.ayfaar.app.Application;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("remote")
@WebAppConfiguration
public class Tagger {
    @Autowired private ItemDao itemDao;
    @Autowired private TermsTaggingUpdater taggingUpdater;

    @Test
    public void items() {
        final List<Item> items = itemDao.getByRegexp("number", "^[6]\\.");
        taggingUpdater.update(items);
    }

    @Test
    public void quotes() {
        taggingUpdater.updateAllQuotes();
    }

    @Test
    public void termDescriptions() {
        taggingUpdater.updateAllTerms();
    }
}

