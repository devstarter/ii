package org.ayfaar.app.synchronization;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.ayfaar.app.model.Category.PARAGRAPH_SIGN;

@Component
@Slf4j
public class TOCSync {
    @Autowired CommonDao commonDao;
    @Autowired CategoryDao categoryDao;
    @Autowired MediaWikiBotHelper mediaWikiBotHelper;

    private StringBuilder sb;
    private PrintStream out;

    public void synchronize() throws UnsupportedEncodingException {
        out = new PrintStream(System.out, true, "UTF-8");

        sb = new StringBuilder();
        List<Category> topCategories = categoryDao.getTopLevel();
        for (Category category : topCategories) {
            syncChildren(category, "=");
        }
        mediaWikiBotHelper.saveArticle("Содержание", sb.toString());
    }

    private void syncChildren(Category parent, String depth) {
        String name = parent.getName();
        String label = name;
        int i = label.lastIndexOf("/");
        if (i > 0) {
            label = label.substring(i+2);
        }

        String line;
        if (parent.isParagraph()) {
            label = PARAGRAPH_SIGN + "" + name.substring(name.lastIndexOf(".")+1);
            line = format("* [[%s|%s. %s]]\n",
                    name,
                    label,
                    parent.getDescription());
        } else {
            line = format("%s [[%s%s|%s. %s]] %s\n",
                    depth,
                    ":Category:",
                    name,
                    label,
                    parent.getDescription() != null ? parent.getDescription() : "",
                    depth);
        }
        sb.append(line);
        out.println(line);
//        if (parent.getDescription() != null && !parent.getDescription().isEmpty()) {
//            sb.append(format("%s\n", parent.getDescription()));
//        }
        List<Category> children = new ArrayList<Category>();
        if (parent.getStart() != null) {
            Category child = categoryDao.get(parent.getStart());
            if (child != null) {
                children.add(child);
                while (child.getNext() != null) {
                    child = categoryDao.get(child.getNext());
                    if (!child.getParent().equals(parent.getUri()))
                        break;
                    children.add(child);
                }
            }
        }
        for (Category category : children) {
            syncChildren(category, depth+"=");
        }
    }
}
