package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
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
        boolean paragraphMode = category.isParagraph();
        SimpleArticle article = new SimpleArticle(paragraphMode ? category.getName() : "Category:"+category.getName());
        validateTitle(article.getTitle());

        StringBuilder sb = new StringBuilder();

        if (paragraphMode) {
            sb.append(format("{{DISPLAYTITLE:%s %s}}\n", category.getName().replace("Параграф", "§"), category.getDescription()));
            Item currentItem = itemDao.get(category.getStart());
            String itemNumber = currentItem.getNumber();
            String endNumber = null;
            if (category.getEnd() != null) {
                endNumber = itemDao.get(category.getEnd()).getNumber();
            }
            do {
                itemSync.scheduleSync(currentItem, category.getName());
                sb.append(format("[[%s]]. {{:%s}}<br /><br />", itemNumber, itemNumber));
                itemNumber = ItemController.getNext(itemNumber);
                currentItem = itemDao.getByNumber(itemNumber);
            } while (endNumber != null && !itemNumber.equals(endNumber));

            if (category.getNext() != null) {
//                String next = getValueFromUri(Category.class, category.getNext());
                Category next = categoryDao.get(category.getNext());
                sb.append(format("Следующая(ий) [[%s|%s %s]]",
                        next.getName(),
                        next.getName().replace(PARAGRAPH_NAME, PARAGRAPH_SIGN),
                        next.getDescription()));
            }
        } else {
            if (category.getDescription() != null) {
                sb.append(format("== %s ==\n", category.getDescription()));
            }
        }

        if (category.getParent() != null) {
            sb.append(format("[[Category:%s]]", getValueFromUri(Category.class, category.getParent())));
        }

        article.setText(sb.toString());
        if (article.getText().isEmpty()) {
            article.setText("1");
        }
        mediaWikiBotHelper.saveArticle(article);
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
