package org.ayfaar.app.synchronization;

import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;
import static org.ayfaar.app.model.Category.PARAGRAPH_NAME;
import static org.ayfaar.app.model.Category.PARAGRAPH_SIGN;
import static org.ayfaar.app.synchronization.SyncUtils.getArticleName;

@Component
public class ItemSync extends EntitySynchronizer<Item> {

    @Autowired AliasesMap aliasesMap;
    @Autowired CommonDao commonDao;
    @Autowired CategoryDao categoryDao;
    @Autowired MediaWikiBotHelper mediaWikiBotHelper;
    @Autowired TermSync termSync;

    public void synchronize(Item item) throws Exception {
        String content = item.getContent();
        Category paragraph = categoryDao.getForItem(item.getUri());

        content = termSync.markTerms(content);

        if (item.getNext() != null || paragraph != null) {
            content += "\n<noinclude>\n";
            if (item.getNext() != null) {
                content += format("[[%s|Следующий пункт]] ", getArticleName(Item.class, item.getNext()));
            }
            if (paragraph != null) {
                String pName = getArticleName(Category.class, paragraph);
                content += format("[[%s|%s]]", pName, pName.replace(PARAGRAPH_NAME+":", PARAGRAPH_SIGN));
            }
            content += "\n</noinclude>";
        }

        mediaWikiBotHelper.saveArticle(getArticleName(Item.class, item.getUri()), content);
    }


    /*public void scheduleSync(Item item, Category paragraph) {
        super.scheduleSync(item);
        paragraphMap.put(item, paragraph);
    }*/
}
