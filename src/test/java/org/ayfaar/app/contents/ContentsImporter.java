package org.ayfaar.app.contents;


import lombok.extern.log4j.Log4j;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ayfaar.app.Application;
import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.ItemsRange;
import org.ayfaar.app.utils.RomanNumber;
import org.ayfaar.app.utils.StringUtils;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
@Log4j
public class ContentsImporter {

    @Inject CommonDao commonDao;
    @Inject ItemDao itemDao;

    @Test
    public void main() throws IOException, XLSParsingException {
        log.info("Открытие файла оглавлений");
        InputStream in = new FileInputStream("./src/test/resources/content/ii-contents-2016.04.12.xlsx");
        log.info("Считывание данных");
        List<ItemBook> list = fillListItemBooks(in);
        in.close();
        System.out.println();
        saveItems(list); //получили готовый массив ItemsRange
        System.out.println();
        //todo: сформировать объекты Category, проставить itemRange.category ссылку на URI категории главы
        //todo: сохранить в БД

    }

    private static ItemBook getTom(Sheet sheet) throws XLSParsingException{
        ItemBook itemBook = new ItemBook();
        //на листе только один том,при этом номер в 1-й строке, название во 2-й строке
        if(sheet.getRow(0).getCell(1,Row.RETURN_BLANK_AS_NULL) != null){

            String number = sheet.getRow(0).getCell(1).toString().toUpperCase();
            if (number.indexOf("ТОМ") > 0){
                itemBook.setType(SectionType.Tom);
                itemBook.setCategoryNumber(number.replaceAll("[^0-9]",""));//из 1-й строки нам нужен только номер тома
            }else{
                throw new IncorrectRowException("Ячейка \"A1\" должна содержать \"Том {Номер тома}\"");
            }
            //название тома берем из следующей строки
            if(sheet.getRow(1).getCell(1,Row.RETURN_BLANK_AS_NULL) != null){
                String title = sheet.getRow(1).getCell(1).toString();
                title = title.replaceAll("^Книга[^\\–]+–(.+)$", "$1");
                itemBook.setTitle(title);
            }else{
                throw new IncorrectRowException("Ячейка \"B2\" должна содержать название тома");
            }
        } else{
            throw new IncorrectRowException("Ячейка \"A1\" должна содержать \"Том {Номер тома}\"");
        }
        return itemBook;
    }

    //парсим xlsx
    private List<ItemBook> fillListItemBooks(InputStream in) throws IOException, XLSParsingException {
        // Using XSSF for xlsx format, for xls use HSSF
        Workbook workbook = new XSSFWorkbook(in);
        int numberOfSheets = workbook.getNumberOfSheets();

        List<ItemBook> categories = new ArrayList<>();
        //создаем список со всеми категориями
        Map<SectionType, ItemBook> currentItems = new HashMap<>();
//        Map<SectionType, ItemBook> prevItems = new HashMap<>();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (!sheet.getSheetName().contains("Том")) continue;
            //на листе только один том, при этом номер в 1-й строке, название во 2-й строке
            final ItemBook tom = getTom(sheet);

            if (tom.getCategoryNumber().equals("5")) continue; // пропускаем 5 том

            categories.add(tom); //добавили том
            currentItems.put(SectionType.Tom, tom);
            final Iterator<Row> rowIterator = sheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() == 0 || row.getRowNum() == 1){
                    continue; //номер и название тома добавили выше (хардкод)
                }
                //по структуре файла - нас интересует только 3 столбца
                Cell cell_0 = row.getCell(0,Row.RETURN_BLANK_AS_NULL);
                Cell cell_1 = row.getCell(1,Row.RETURN_BLANK_AS_NULL);
                Cell cell_2 = row.getCell(2,Row.RETURN_BLANK_AS_NULL);
                //если все значения null - тогда ничего добавлять не надо
                if(cell_0 == null && cell_1 == null && cell_2 == null) {//пустых ячеек не может быь из-за Row.RETURN_BLANK_AS_NULL
                    continue;//если в ячейках данной строки нет значений, просто пропускаем ее
                }
                ItemBook itemBook = new ItemBook();
                //если есть значение в 1-м столбце - тогда это параграф
                if (cell_0 != null) {
                    if (cell_1 == null || cell_2 == null){
                        if (cell_0.toString().startsWith("0.")) continue;
                        throw new IncorrectRowException("Неверная строка №"+(row.getRowNum() + 1) + " для параграфа, не заполнено либо название, либо номер абзаца");
                    }
                    itemBook.setCategoryNumber(cell_0.toString().trim());
                    itemBook.setType(SectionType.Paragraph);
                    itemBook.setTitle(cell_1.toString().trim());
                    String itemNumber = cell_2.toString().trim();
                    if (itemNumber.endsWith(".0")) {
                        itemNumber = itemNumber.replace(".0", "");
                    }
                    if (itemNumber.contains("-")) {
                        itemNumber = itemNumber.replaceAll("^([^\\-]+)-.+$", "$1"); // 10001-10006 -> 10001
                    }
                    if (!itemNumber.contains(".")) {
                        itemNumber = tom.getCategoryNumber() + "." + itemNumber; // 10001 -> 10.10001
                    }
                    itemNumber = itemNumber.replaceAll("^(\\d+\\.)(\\d{3})$", "$10$2"); //1.786 -> 1.0786
                    itemBook.setItemNumber(itemNumber);
                    itemBook.setParent(currentItems.get(SectionType.Chapter));
                    itemBook.setPrev(putOrReplace(currentItems, itemBook));
                    currentItems.get(SectionType.Chapter).setStartIfEmpty(itemBook);
                } else { //первый столбец пустой - тогда это том, раздел или глава. Т.к. том может быть только в первых 2 стрках, то остается только "раздел" или "глава"
                    if (cell_1 == null){ // добавить проверку на пустую строку
                        //неверная строка для раздела или тома(в данном случае будет заполнен 3-й столббец, иначе бы прошли по условию когда все ячейки пустые)
                        //выбрасываем исключение или continue
                        throw new IncorrectRowException("Неверная строка №" + (row.getRowNum() + 1) + " для раздела или тома, из всей строке заполнен только 3-й столббец");
                    }
                    //если поле начинается с "Раздел" - то добавлем раздел
                    String str = cell_1.toString();
                    if(str.toLowerCase().startsWith("раздел ")) {
                        int newLine = str.indexOf("\n");
                        String numberOfSection;
                        String title;
                        if (newLine > 0) {
                            numberOfSection = str.substring("раздел ".length(),newLine).trim();
                            title = str.substring(newLine + 1, str.length()).trim();
                        } else {
                            numberOfSection = str.toLowerCase().replace("раздел ", "").trim().toUpperCase();
                            row = rowIterator.next();
                            title = row.getCell(1,Row.RETURN_BLANK_AS_NULL).toString();
                        }
                        numberOfSection = StringUtils.trim(numberOfSection, ".").replace("Х", "X");
                        itemBook.setCategoryNumber(numberOfSection);
                        itemBook.setTitle(title);
                        itemBook.setType(SectionType.Section);
                        itemBook.setParent(currentItems.get(SectionType.Tom));
                        itemBook.setPrev(putOrReplace(currentItems, itemBook));
                        currentItems.get(SectionType.Tom).setStartIfEmpty(itemBook);
                    } else if(str.toLowerCase().startsWith("глава ")){
                        int dot = str.indexOf(".");
                        String numberOfChapter = str.substring("глава ".length(),dot).trim();
                        numberOfChapter = numberOfChapter.replace("№", "");
                        itemBook.setCategoryNumber(numberOfChapter);
                        itemBook.setTitle(str.substring(dot + 1, str.length()).trim());
                        itemBook.setType(SectionType.Chapter);
                        itemBook.setParent(currentItems.get(SectionType.Section));
                        itemBook.setPrev(putOrReplace(currentItems, itemBook));
                        currentItems.get(SectionType.Section).setStartIfEmpty(itemBook);
                    }else {
                        continue; // just ignore
//                        throw new IncorrectRowException("Неверная строка №" + (row.getRowNum() + 1) + ". Данная строка не являесят ни Томом, ни разделом, ни главой, ни параграфом");
                    }
                }
                validate(itemBook);
                categories.add(itemBook);
            }
        }
        return categories;
    }

    private ItemBook putOrReplace(Map<SectionType, ItemBook> map, ItemBook item) {
        return map.containsKey(item.type) ? map.replace(item.type, item) : map.put(item.type, item);
    }

    private void validate(ItemBook itemBook) {
        Assert.hasLength(itemBook.getTitle(), "..."); //todo: ... заменить на текст
        Assert.hasLength(itemBook.getCategoryNumber(), "...");
        Assert.notNull(itemBook.getType(), "...");
        if (itemBook.type == SectionType.Paragraph) {
            Assert.notNull(itemBook.getParent(), "...");
            String itemNumber = itemBook.getItemNumber();
            Assert.isTrue(Item.isItemNumber(itemNumber), itemNumber + " not a item number");
        }
        if (itemBook.type == SectionType.Section) {
            Assert.isTrue(RomanNumber.parse(itemBook.getCategoryNumber()) > 0);
        }
        if (itemBook.type == SectionType.Chapter) {
            Assert.isTrue(NumberUtils.isNumber(itemBook.getCategoryNumber()), itemBook.getCategoryNumber() + " not a number");
        }
    }

    private void saveItems(List<ItemBook> itemBookList) throws XLSParsingException{
        Map<ItemBook, Category> categories = new LinkedHashMap<>();
        List<ItemsRange> ranges = new ArrayList<>();
        Map<ItemBook, ItemsRange> itemRange = new LinkedHashMap<>();

        itemBookList.forEach(item -> {
            String name = null;
            if (item.getType() == SectionType.Paragraph) {
                name = String.format("%s.%s.%s",
                        item.getParent().getParent().getParent().getCategoryNumber(),
                        RomanNumber.parse(item.getParent().getParent().getCategoryNumber()),
                        item.getCategoryNumber());
                name = StringUtils.trim(name, ".");

                ItemsRange itemsRange = commonDao.getOpt(ItemsRange.class, UriGenerator.generate(ItemsRange.class, name))
                        .orElse(ItemsRange.builder().code(name).build());
                itemsRange.setDescription(item.getTitle());
                itemsRange.setFrom(item.getItemNumber());
                itemsRange.setCategory(categories.get(item.getParent()).getUri());
                validate(itemsRange);
                ranges.add(commonDao.save(itemsRange));
                itemRange.put(item, itemsRange);
            } else {
                switch (item.getType()) {
                    case Tom:
                        name = "Том " + item.getCategoryNumber();
                        break;
                    case Section:
                        name = String.format("%s/Раздел %s",
                                Integer.valueOf(item.getParent().getCategoryNumber()) > 9 ? "БДК" : "Основы",
                                item.getCategoryNumber());
                        break;
                    case Chapter:
                        name = String.format("%s/Раздел %s/Глава %s",
                                Integer.valueOf(item.getParent().getParent().getCategoryNumber()) > 9 ? "БДК" : "Основы",
                                item.getParent().getCategoryNumber(),
                                item.getCategoryNumber());
                        break;
                }
                String uri = UriGenerator.generate(Category.class, name);
                Category category = commonDao.getOpt(Category.class, uri).orElse(Category.builder().name(name).build());
                category.setDescription(item.getTitle());
                if (item.getType() == SectionType.Tom) {
                    String parentName = Integer.valueOf(item.getCategoryNumber()) > 9 ? "БДК" : "Основы";
                    String parentUri = UriGenerator.generate(Category.class, parentName);
                    category.setParent(parentUri);
                } else {
                    category.setParent(categories.get(item.getParent()).getUri());
                }
                categories.put(item, commonDao.save(category));
            }
        });

        final Map<SectionType, List<Map.Entry<ItemBook, Category>>> typeCategoryMap = StreamEx.of(categories.entrySet()).groupingBy(e -> e.getKey().getType());

        StreamEx.of(typeCategoryMap.entrySet()).forEachOrdered(e -> {
            final Iterator<Category> iterator = StreamEx.of(e.getValue())
                    .map(Map.Entry::getValue)
                    .iterator();
            Category current = null;
            while (iterator.hasNext()) {
                final Category next = iterator.next();
                if (current != null) {
                    current.setNext(next.getUri());
                    commonDao.save(current);
                }
                current = next;
            }
        });

        final Map<String, List<ItemsRange>> rangesByTom = StreamEx.of(ranges).groupingBy(r -> r.getFrom().replaceAll("^(\\d+)\\.\\d+", "$1"));

        rangesByTom.entrySet().forEach(e -> {
            final Iterator<ItemsRange> iterator = e.getValue().iterator();
            ItemsRange current = null;
            while (iterator.hasNext()) {
                final ItemsRange next = iterator.next();
                if (current != null) {
                    current.setTo(ItemController.getPrev(next.getFrom()));
                    commonDao.save(current);
                }
                current = next;
            }
            final String tomLastItemNumber = itemDao.getTomLastItemNumber(e.getKey());
            current.setTo(tomLastItemNumber);
            commonDao.save(current);
        });
        categories.forEach((item, category) -> {
            String start;
            if (item.type == SectionType.Chapter) {
                start = item.getStart() == null ? null : itemRange.get(item.getStart()).getUri();
            } else {
                start = categories.get(item.getStart()).getUri();
            }
            category.setStart(start);
            commonDao.save(category);
        });
    }

    private static void validate(ItemsRange range) {
        Assert.isTrue(Item.isItemNumber(range.getFrom()), "...");
//        Assert.isTrue(Item.isItemNumber(range.getTo()), "...");
        Assert.isTrue(range.getCode().matches("^(\\d+\\.){3}\\d+$"), "Неверный код "+range.getCode());
        Assert.hasLength(range.getDescription(), "...");
        Assert.hasLength(range.getCategory(), "...");
    }
}