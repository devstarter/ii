package org.ayfaar.app.synchronization.mediawiki;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static java.lang.String.format;
import static org.ayfaar.app.model.Category.PARAGRAPH_SIGN;
import static org.ayfaar.app.synchronization.mediawiki.SyncUtils.getArticleName;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
@Slf4j
public class TOCSync {
    @Autowired CommonDao commonDao;
    @Autowired CategoryDao categoryDao;
    @Autowired MediaWikiBotHelper mediaWikiBotHelper;
    @Autowired CategorySync categorySync;

    public static final String NS_NAME = "Содержание";

    private StringBuilder sb;
    private PrintStream out;
    private Queue<Category> queue;

    public TOCSync() throws UnsupportedEncodingException {
        out = new PrintStream(System.out, true, "UTF-8");
    }

    public void synchronize() throws UnsupportedEncodingException {
        sb = new StringBuilder();
        List<Category> topCategories = categoryDao.getTopLevel();
        List<Category> toms = new ArrayList<Category>();

        for (Category category : topCategories) {
            for (Category child : getChildren(category)) {
                setLine(child, "");
                toms.add(child);
            }
        }
//        mediaWikiBotHelper.saveArticle(NS_NAME+":Тома", sb.toString());

        queue = new ArrayDeque<Category>(toms);
        Category category = queue.poll();
        while (category != null) {
            sb = new StringBuilder();
            for (Category child : getChildren(category)) {
                recursiveCompute(child, "=");
            }
            if (category.getParent() != null) {
                Category parent = categoryDao.get(category.getParent());
                sb.append(format("\n[[%s|%s%s]]",
                        getArticleName(Category.class, parent.getUri()),
                        getValueFromUri(Category.class, parent.getUri()),
                        parent.getDescription() != null ? ". "+parent.getDescription() : ""));
            }
            mediaWikiBotHelper.saveArticle(NS_NAME + ":" + category.getName(), sb.toString());
            category = queue.poll();
        }

    }

    private void recursiveCompute(Category category, String depth) {
        if (!category.isParagraph()) {
            queue.add(category);
        }
        setLine(category, depth);
        for (Category child : getChildren(category)) {
            recursiveCompute(child, depth+"=");
        }
    }

    private void setLine(Category category, String depth) {
        String name = category.getName();
        String label = name;
        int i = label.lastIndexOf("/");
        if (i > 0) {
            label = label.substring(i+2);
        }

        String line;
        if (category.isParagraph()) {
            categorySync.scheduleSync(category);
            label = PARAGRAPH_SIGN + "" + name.substring(name.lastIndexOf(".")+1);
            line = format("* [[%s|%s. %s]]\n",
                    getArticleName(Category.class, category),
                    label,
                    category.getDescription());
        } else {
            line = format("%s[[%s:%s|%s%s]]%s\n",
                    depth.isEmpty() ? "" : depth+" ",
                    NS_NAME,
                    name,
                    label,
                    category.getDescription() != null ? ". "+category.getDescription() : "",
                    depth.isEmpty() ? "" : " "+depth);
        }
        if (category.isTom()) {
            line = "* "+line;
        }
        sb.append(line);
    }

    private List<Category> getChildren(Category parent) {
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
        return children;
    }
}
