package org.ayfaar.app.importing;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.synchronization.CategorySync;
import org.ayfaar.app.synchronization.ItemSync;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CategoryExporterTest {

    private static ApplicationContext ctx;

    public static void main(String[] args) throws Exception {
        ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);

        CommonDao commonDao = ctx.getBean(CommonDao.class);

        if (false) {
            CategorySync categorySync = ctx.getBean(CategorySync.class);
            categorySync.synchronize(commonDao.get(Category.class, "категория:Параграф 10.1.1.1"));
            for (Category category : commonDao.getAll(Category.class)) {
                categorySync.synchronize(category);
            }

        }
            ItemSync itemSync = ctx.getBean(ItemSync.class);
            for (Item item : commonDao.getLike(Item.class, "number", "10.%", 1000000)) {
                itemSync.synchronize(item);
            }
//        itemSync.synchronize(commonDao.get(Item.class, "ии:пункт:1.0004"));

//        TermSync termSync = ctx.getBean(TermSync.class);
//        termSync.synchronize(commonDao.get(Term.class, "ии:термин:Время"));
//        for (String termUri : itemSync.foundTerms) {
//            termSync.synchronize(commonDao.get(Term.class, termUri));
//        }
    }
}
