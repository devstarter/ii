package org.ayfaar.app.importing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
public class CategoryImporter {

    CommonDao commonDao;
    ItemDao itemDao;
    private ApplicationContext ctx;
    private HashMap<String, Category> categoriesMap;

    @Test
    public void categoryImport() throws IOException {
        ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        commonDao = ctx.getBean(CommonDao.class);
        itemDao = ctx.getBean(ItemDao.class);

        categoriesMap = new HashMap<String, Category>();
        Category paragraphCat = null;
        Category typeCat = null;
        Category tomCat = null;
        Category razdelCat = null;
        Category glavaCat = null;

        for(String line: FileUtils.readLines(new File("D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\text\\paragraphs\\Параграфы, БДК, Том 10.csv"))) {
            String[] columns = line.split(";");
            String type = columns[0];
            String tom = columns[1];
            int tomNumber = Integer.valueOf(tom.replace("Том ", ""));
            String razdel = columns[2];
            String glava = columns[3];
            String paragraphNumber = columns[4];
            String paragraphDescription = columns[5];
            String[] items = columns[6].split("-");
            String itemFrom = tomNumber+"."+items[0];
            String itemTo = tomNumber+"."+items[1];

            typeCat = getCat(type, typeCat, null);
            tomCat = getCat(tom, tomCat, typeCat);
            razdelCat = getCat(razdel, razdelCat, tomCat);
            glavaCat = getCat(glava, glavaCat, razdelCat);

            Category prevParagraphCat = paragraphCat;
            String paragraphName = "Параграф "+paragraphNumber;
            paragraphCat = commonDao.get(Category.class, "name", paragraphName);
            if (paragraphCat == null) {
                paragraphCat = new Category(paragraphName, glavaCat.getUri());
                paragraphCat.setDescription(paragraphDescription);
                paragraphCat.setStart(itemDao.getByNumber(itemFrom).getUri());
                paragraphCat.setEnd(itemDao.getByNumber(itemTo).getUri());
                commonDao.save(paragraphCat);
                log.info(paragraphCat.getName());
            }
            if (prevParagraphCat != null) {
                prevParagraphCat.setNext(paragraphCat.getUri());
                commonDao.save(prevParagraphCat);
            }
        }
    }

    private Category getCat(String categoryName, Category prevCategory, Category parent) {
        Category category = categoriesMap.get(categoryName);
        if (category == null) {
            category = commonDao.get(Category.class, "name", categoryName);
            if (category == null) {
                category = commonDao.save(new Category(categoryName, parent != null ? parent.getUri() : null));
            }
            categoriesMap.put(categoryName, category);
        }
        if (prevCategory != null && !category.equals(prevCategory)) {
            prevCategory.setNext(category.getUri());
            commonDao.save(prevCategory);
        }
        return category;
    }


}
