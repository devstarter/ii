package org.ayfaar.app.importing;

import org.apache.commons.io.FileUtils;
import org.ayfaar.app.Application;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ItemsHelper;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("remote")
@WebAppConfiguration
public class ItemsTagger {
    @Autowired private ItemDao itemDao;
    @Autowired private TermsTaggingUpdater taggingUpdater;

    @Test
    public void main() throws IOException {
        final List<Item> items = itemDao.getByRegexp("number", "^5");
        taggingUpdater.updateItems(items);
    }
}

