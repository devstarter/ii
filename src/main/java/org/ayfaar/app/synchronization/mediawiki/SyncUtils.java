package org.ayfaar.app.synchronization.mediawiki;

import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SyncUtils {
    public static String getArticleName(Class<? extends UID> objectClass, UID entity) {
        return getArticleName(objectClass, entity.getUri());
    }
    public static String getArticleName(Class<? extends UID> objectClass, String uri) {
        String name = UriGenerator.getValueFromUri(objectClass, uri);
        if (Item.class.equals(objectClass)) {
            name = "Пункт:"+name;
        }
        if (Category.class.equals(objectClass)) {
            if (name.startsWith(Category.PARAGRAPH_NAME)) {
                name = name.replace(" ", ":");
            } else {
                name = TOCSync.NS_NAME+":"+name;
            }
        }
        return name;
    }

    @Autowired TermSync termSync;
    @Autowired CategorySync categorySync;
    @Autowired ItemSync itemSync;

    public void scheduleSync(UID entity) {
        EntitySynchronizer synchronizer = null;
        if (entity instanceof Term) {
            synchronizer = termSync;
        } else if (entity instanceof Item) {
            synchronizer = itemSync;
        } else if (entity instanceof Category) {
            synchronizer = categorySync;
        }
        if (synchronizer != null) {
            synchronizer.scheduleSync(entity);
        }
    }

    public void syncAllScheduled() throws Exception {
        termSync.syncScheduled();
        itemSync.syncScheduled();
        categorySync.syncScheduled();
    }
}
