package org.ayfaar.app.importing;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.synchronization.CategorySync;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CategoryExporterTest {

    private static ApplicationContext ctx;

    public static void main(String[] args) throws Exception {
        ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);

        CommonDao commonDao = ctx.getBean(CommonDao.class);
        CategorySync categorySync = ctx.getBean(CategorySync.class);

        for (Category category : commonDao.getAll(Category.class)) {
            categorySync.synchronize(category);
        }
    }
}
