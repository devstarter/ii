package org.ayfaar.app.importing;

import au.com.bytecode.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;

import static java.util.Arrays.asList;

@Slf4j
public class CategoryImporter {

    static CommonDao commonDao;
    static ItemDao itemDao;
    private static ApplicationContext ctx;
    private static HashMap<String, Category> categoriesMap;

    public static void main(String[] args) throws Exception {
        ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        commonDao = ctx.getBean(CommonDao.class);
        itemDao = ctx.getBean(ItemDao.class);

        categoriesMap = new HashMap<String, Category>();
        Category paragraphCat = null;
        Category typeCat = null;
        Category tomCat = null;
        Category razdelCat = null;
        Category glavaCat = null;

//        List<String> lines = FileUtils.readLines(new File("D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\text\\paragraphs\\Параграфы, БДК, Том 10,14.utf.csv"));
        CSVReader reader = new CSVReader(new FileReader("D:\\projects\\ayfaar\\ii-app\\src\\text\\содержание\\formated\\3 том.csv"), ';');
        String [] nextLine;
        reader.readNext(); // skip header
        while ((nextLine = reader.readNext()) != null){
            Iterator<String> data = asList(nextLine).iterator();
            String cikl = data.next().trim();
            String tom = data.next().trim();
            int tomNumber = Integer.valueOf(tom.replace("Том ", "").trim());
            String razdelName = data.next().trim();
            String razdelCode = cikl+"/"+razdelName;
            String razdelaDesc = data.next().trim();
            String glavaFull = data.next().trim();
            final int i = glavaFull.indexOf(". ");
            String glavaName = glavaFull.substring(0, i - 1);
            String glavaCode = razdelCode+"/"+glavaName;
            String glavaDesc = glavaFull.substring(i);
            String paragraphNumber = data.next().trim();
            String paragraphDescription = data.next().trim();
            String[] items = data.next().split("-");

            String item = items[0];
            if (!item.contains(".")) {
                item = tomNumber+"."+item;
            }
            String itemTo;

            if (items.length == 1) {
                itemTo = null;
            } else {
                itemTo = tomNumber+"."+items[1];
            }

            typeCat = getCat(cikl, typeCat, null);
            tomCat = getCat(tom, tomCat, typeCat);
            razdelCat = getCat(razdelCode, razdelaDesc, razdelCat, tomCat);
            glavaCat = getCat(glavaCode, glavaDesc, glavaCat, razdelCat);

            if (typeCat.getStart() == null) {
                typeCat.setStart(tomCat.getUri());
                commonDao.save(typeCat);
            }
            if (tomCat.getStart() == null) {
                tomCat.setStart(razdelCat.getUri());
                commonDao.save(tomCat);
            }
            if (razdelCat.getStart() == null) {
                razdelCat.setStart(glavaCat.getUri());
                commonDao.save(razdelCat);
            }

            Category prevParagraphCat = paragraphCat;
            String paragraphName = "Параграф "+paragraphNumber;
            paragraphCat = commonDao.get(Category.class, "name", paragraphName);
            if (paragraphCat == null) {
                paragraphCat = new Category(paragraphName, glavaCat.getUri());
                paragraphCat.setDescription(paragraphDescription);
                Item number = itemDao.getByNumber(item);
                paragraphCat.setStart(number.getUri());
                if (itemTo != null) {
                    Item byNumber = itemDao.getByNumber(itemTo);
                    paragraphCat.setEnd(byNumber.getUri());
                }
                commonDao.save(paragraphCat);
                System.out.println("new " + paragraphCat.getName());
            }
            if (prevParagraphCat != null && !paragraphCat.getUri().equals(prevParagraphCat.getNext())) {
                prevParagraphCat.setNext(paragraphCat.getUri());
                if (prevParagraphCat.getEnd() == null) {
                    prevParagraphCat.setEnd(paragraphCat.getStart());
                }
                commonDao.save(prevParagraphCat);
                System.out.println("update "+paragraphCat.getName());
            }
            if (glavaCat.getStart() == null) {
                glavaCat.setStart(paragraphCat.getUri());
                commonDao.save(glavaCat);
            }
        }
    }

    private static Category getCat(String categoryUniqCode, Category prevCategory, Category parent) {
        return getCat(categoryUniqCode, null, prevCategory, parent);
    }
    private static Category getCat(String categoryUniqCode, String description,
                                   Category prevCategory, Category parent) {
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
                if (description != null) {
                    description = description.trim();
                }
                category = commonDao.save(new Category(categoryUniqCode, description, parent != null ? parent.getUri() : null));
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
