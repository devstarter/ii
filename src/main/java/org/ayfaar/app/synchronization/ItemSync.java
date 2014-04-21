package org.ayfaar.app.synchronization;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Component
public class ItemSync extends EntitySynchronizer<Item> {

    @Autowired AliasesMap aliasesMap;
    @Autowired CommonDao commonDao;
    @Autowired MediaWikiBotHelper mediaWikiBotHelper;
    @Autowired TermSync termSync;

    private Map<Item, String> paragraphMap = new HashMap<Item, String>();

    public void synchronize(Item item) throws Exception {
        String content = item.getContent();
        String paragraph = paragraphMap.get(item);

        content = termSync.markTerms(content);

        if (item.getNext() != null || paragraph != null) {
            content += "\n<noinclude>\n";
            if (item.getNext() != null) {
                content += format("[[%s|Следующий пункт]] ", UriGenerator.getValueFromUri(Item.class, item.getNext()));
            }
            if (paragraph != null) {
                content += format("[[%s]]", paragraph);
            }
            content += "\n</noinclude>";
        }

        mediaWikiBotHelper.saveArticle(item.getNumber(), content);
//        item.setWiki(content);
//        commonDao.save(item);
    }


    public void scheduleSync(Item item, String paragraph) {
        super.scheduleSync(item);
        paragraphMap.put(item, paragraph);
    }
}
