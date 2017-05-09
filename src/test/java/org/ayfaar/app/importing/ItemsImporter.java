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
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("remote")
@WebAppConfiguration
public class ItemsImporter {
    private Item currentItem;
    private Item prevItem;
    @Autowired private ApplicationContext ctx;
//    private String skipUntilNumber = "1.0780";
    private boolean saveAllowed = true;
    @Autowired private ItemDao itemDao;
    @Autowired private TermsTaggingUpdater taggingUpdater;

    @Test
    public void main() throws IOException {
        currentItem = null;

        for(String line: FileUtils.readLines(new File("C:\\PROJECTS\\ayfaar\\texts\\Том 5.txt"))) {

            Matcher matcher = compile("(\\d+\\.\\d\\d\\d\\d+)\\.\\s(.+)").matcher(line);
            if (matcher.find()) {
                if (currentItem != null && saveAllowed) {
                    saveItem();
                }
                currentItem = new Item(matcher.group(1), matcher.group(2));
//                saveAllowed = saveAllowed || currentItem.getNumber().equals(skipUntilNumber);
            } else if (currentItem != null) {
                currentItem.setContent(currentItem.getContent() + "\n" + line);
            }
        }
        saveItem();
    }

    private void saveItem() {
        System.out.print(currentItem.getNumber() + "\n");
//        System.out.println(currentItem.getContent());

        Item storedItem = itemDao.getByNumber(currentItem.getNumber());
        if (storedItem != null) {
            storedItem.setContent(currentItem.getContent());
            currentItem = storedItem;
//            currentItem.setUri(UriGenerator.generate(currentItem));
        }
        currentItem.setContent(currentItem.getContent().trim());
        currentItem.setContent(ItemsHelper.clean(currentItem.getContent()));
//        taggingUpdater.update(currentItem); //saved inside update
        itemDao.save(currentItem);

        if (prevItem != null) {
            prevItem.setNext(currentItem.getUri());
            itemDao.save(prevItem);
        }

        prevItem = currentItem;
    }
}

