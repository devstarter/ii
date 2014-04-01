package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ParagraphHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class CategorySync implements EntitySynchronizer<Category> {
    @Autowired MediaWikiBotProvider mediaWikiBotProvider;
    @Autowired ParagraphHelper paragraphHelper;
    @Autowired ItemDao itemDao;
    @Autowired ItemSync itemSync;

    @Override
    public void synchronize(Category category) throws Exception {
        boolean paragraphMode = category.getStart() != null;
        SimpleArticle article = new SimpleArticle(paragraphMode ? category.getName() : "Category:"+category.getName());
        validateTitle(article.getTitle());
        PrintStream out = new PrintStream(System.out, true, "UTF-8");

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
                itemSync.synchronize(currentItem);
                sb.append(format("[[%s]]. {{:%s}}<br /><br />", itemNumber, itemNumber));
                itemNumber = ItemController.getNext(itemNumber);
                currentItem = itemDao.getByNumber(itemNumber);
            } while (endNumber != null && !itemNumber.equals(endNumber));

            if (category.getNext() != null) {
                String next = getValueFromUri(Category.class, category.getNext());
                sb.append(format("Следующая(ий) [[%s|%s]]", next, next.replace("Параграф", "§")));
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
//        try {
            mediaWikiBotProvider.getBot().writeContent(article);
//            System.out.println(article.getTitle());

        out.println(article.getTitle());
//        } catch (Exception e) {
            //mediaWikiBotProvider.getBot().writeContent(article);
//        }
//        Thread.sleep(1000);
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
