package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.utils.ParagraphHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class CategorySync {
    @Autowired MediaWikiBotProvider mediaWikiBotProvider;
    @Autowired ParagraphHelper paragraphHelper;
    @Autowired ItemDao itemDao;

    public void synchronize(Category category) throws Exception {
        SimpleArticle article = new SimpleArticle("Category:"+category.getName());
        validateTitle(article.getTitle());
        PrintStream out = new PrintStream(System.out, true, "UTF-8");

        StringBuilder sb = new StringBuilder();
//        sb.append("= ").append(category.getName()).append(" =\n");

        if (category.getDescription() != null) {
            sb.append(format("== %s ==\n", category.getDescription()));
        }

        if (category.getStart() != null) {
            article.setTitle(category.getName());
            String itemNumber = itemDao.get(category.getStart()).getNumber();
            String endNumber = itemDao.get(category.getEnd()).getNumber();
            do {
                sb.append(format("[[%s]]. {{:%s}}<br /><br />", itemNumber, itemNumber));
                itemNumber = ItemController.getNext(itemNumber);
            } while (!itemNumber.equals(endNumber));
            if (category.getNext() != null) {
                sb.append(format("Следующая(ий) [[%s]]", getValueFromUri(Category.class, category.getNext())));
            }
        }

        if (category.getParent() != null) {
            sb.append(format("[[Category:%s]]", getValueFromUri(Category.class, category.getParent())));
        }

        article.setText(sb.toString());
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
