package org.ayfaar.app.importing;

import au.com.bytecode.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

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

//        List<String> lines = FileUtils.readLines(new File("D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\text\\paragraphs\\Параграфы, БДК, Том 10,14.utf.csv"));
        CSVReader reader = new CSVReader(new FileReader("D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\text\\paragraphs\\Параграфы, БДК, Том 10,14.utf.csv"), ';');
        String [] nextLine;
        List<String> columns;
//        List myEntries = reader.readAll();
//        ListIterator iterator = myEntries.listIterator();
        while ((nextLine = reader.readNext()) != null){
            columns = asList(nextLine);
            String cikl = columns.get(0);
            String tom = columns.get(1);
            int tomNumber = Integer.valueOf(tom.replace("Том ", ""));
            String codeRazdela = columns.get(2);
            String uniqCodeRazdela = columns.get(3);
            String nameRazdela = columns.get(4);
            String codeGlavy = columns.get(6);
            String uniqCodeGlavy = columns.get(7);
            String nameGlavy = columns.get(8);
            String paragraphNumber = columns.get(9);
            String paragraphDescription = columns.get(10);
            String[] items = columns.get(11).split("-");

            String itemFrom = tomNumber+"."+items[0];
            String itemTo = null;

            if (items.length == 1) {
                itemTo = null;
            } else {
                itemTo = tomNumber+"."+items[1];
            }

            typeCat = getCat(cikl, typeCat, null);
            tomCat = getCat(tom, tomCat, typeCat);
            razdelCat = getCat(uniqCodeRazdela, nameRazdela, razdelCat, tomCat);
            glavaCat = getCat(uniqCodeGlavy, nameGlavy, glavaCat, razdelCat);

            Category prevParagraphCat = paragraphCat;
            String paragraphName = "Параграф "+paragraphNumber;
            paragraphCat = commonDao.get(Category.class, "name", paragraphName);
            if (paragraphCat == null) {
                paragraphCat = new Category(paragraphName, glavaCat.getUri());
                paragraphCat.setDescription(paragraphDescription);
                Item number = itemDao.getByNumber(itemFrom);
                paragraphCat.setStart(number.getUri());
                if (itemTo != null) {
                    Item byNumber = itemDao.getByNumber(itemTo);
                    paragraphCat.setEnd(byNumber.getUri());
                }
                commonDao.save(paragraphCat);
                log.info(paragraphCat.getName());
            }
            if (prevParagraphCat != null) {
                prevParagraphCat.setNext(paragraphCat.getUri());
                commonDao.save(prevParagraphCat);
            }
        }
    }

    private Category getCat(String categoryUniqCode, Category prevCategory, Category parent) {
        return getCat(categoryUniqCode, null, prevCategory, parent);
    }
    private Category getCat(String categoryUniqCode, String categoryName, Category prevCategory, Category parent) {
//        String[] split = categoryName.split("\\.\\s");
//        categoryName = split[0];
//        String categoryDescription = null;
//        if (split.length > 1) {
//            categoryDescription = split[1];
//        }
        Category category = categoriesMap.get(categoryUniqCode);
        if (category == null) {
            category = commonDao.get(Category.class, "name", categoryUniqCode);
            if (category == null) {
                category = commonDao.save(new Category(categoryUniqCode, categoryName, parent != null ? parent.getUri() : null));
            }
            categoriesMap.put(categoryUniqCode, category);
        }
        if (prevCategory != null && !category.equals(prevCategory)) {
            prevCategory.setNext(category.getUri());
            commonDao.save(prevCategory);
        }
        return category;
    }


}
