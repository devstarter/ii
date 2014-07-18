package org.ayfaar.app.synchronization.mediawiki;

import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ParagraphHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.ayfaar.app.model.Category.PARAGRAPH_NAME;
import static org.ayfaar.app.model.Category.PARAGRAPH_SIGN;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class CategorySync extends EntitySynchronizer<Category> {
    @Autowired MediaWikiBotHelper mediaWikiBotHelper;
    @Autowired ParagraphHelper paragraphHelper;
    @Autowired ItemDao itemDao;
    @Autowired ItemSync itemSync;
    @Autowired CategoryDao categoryDao;

    @Override
    public void synchronize(Category category) throws Exception {
        if (!category.isParagraph()) {
            throw new RuntimeException("Should be paragraph");
        }
        String articleName = SyncUtils.getArticleName(Category.class, category.getUri());
//        mediaWikiBotHelper.isSyncNeeded(articleName);
        validateTitle(articleName);

        StringBuilder sb = new StringBuilder();

        if (category.isParagraph()) {
            sb.append(format("{{DISPLAYTITLE:%s %s}}\n", category.getName().replace("Параграф", "§"), category.getDescription()));
            Item currentItem = itemDao.get(category.getStart());
            String itemNumber = currentItem.getNumber();
            String endNumber = null;
            if (category.getEnd() != null) {
                endNumber = itemDao.get(category.getEnd()).getNumber();
            }
            do {
                itemSync.scheduleSync(currentItem);
                String itemId = SyncUtils.getArticleName(Item.class, currentItem.getUri());
                sb.append(format("[[%s|%s]]. {{:%s}}<br/><br/>\n",
                        itemId,
                        itemNumber,
                        itemId));
                itemNumber = ItemController.getNext(itemNumber);
                currentItem = itemDao.getByNumber(itemNumber);
            } while (endNumber != null && !itemNumber.equals(endNumber));

            if (category.getNext() != null) {
//                String next = getValueFromUri(Category.class, category.getNext());
                Category next = categoryDao.get(category.getNext());
                sb.append(String.format("[[%s|Следующий %s %s]]\n",
                        SyncUtils.getArticleName(Category.class, next.getUri()),
                        next.getName().replace(PARAGRAPH_NAME, PARAGRAPH_SIGN),
                        next.getDescription()));
            }
        } else {
            if (category.getDescription() != null) {
                sb.append(format("== %s ==\n", category.getDescription()));
            }
        }

        if (category.getParent() != null) {
            Category parent = categoryDao.get(category.getParent());
            sb.append(String.format("\n[[%s|%s. %s]]",
                    SyncUtils.getArticleName(Category.class, parent.getUri()),
                    getValueFromUri(Category.class, parent.getUri()),
                    parent.getDescription()));
        }

        mediaWikiBotHelper.saveArticle(articleName, sb.toString());
    }

    private static final Pattern INVALID_CHARS_PATTERN =
            Pattern.compile("[#{}<>\\[\\]\\|]");

    private void validateTitle(String title) throws Exception {
        if (title == null || title.isEmpty()) {
            throw new Exception("Title should not be empty");
        }
        if (title.length() > 144) {
            throw new Exception("Title length should be less then 144");
        }
        // http://en.wikipedia.org/wiki/Wikipedia:Naming_conventions_(technical_restrictions)
        if (INVALID_CHARS_PATTERN.matcher(title).find()) {
            throw new Exception("Title should not contains # < > [ ] | { }");
        }
    }

}
