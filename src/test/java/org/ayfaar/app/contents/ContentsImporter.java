package org.ayfaar.app.contents;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


import java.io.*;
import java.util.*;

/**
 * Filling DB from xlsx files
 * @author Ruav
 *
 */
public class ContentsImporter {
    static CommonDao commonDao;
    static ItemDao itemDao;
    private static ApplicationContext ctx;
    private static HashMap<String, ItemBook> itemBookMap;

    public static void main(String[] args) throws IOException {

        ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        commonDao = ctx.getBean(CommonDao.class);
        itemDao = ctx.getBean(ItemDao.class);

        InputStream in = new FileInputStream("./src/test/resources/content/5tom-content.xlsx");

        // Заполняем список элементов книги
        List<ItemBook> itemBookList = fillListItemBooks(in);
        in.close();


        for (ItemBook item : itemBookList) {
            System.out.print(item.getType() == ItemBook.TypeSection.Chapter ? "Глава: " : item.getType() == ItemBook.TypeSection.Root ? "Основы: " : item.getType() == ItemBook.TypeSection.Section
                    ? "Раздел: " : "Параграф: " + item.getName() + " ");
            System.out.println(item.getDescription() + " " + (item.getType() == ItemBook.TypeSection.Paragraph ? item.getCode() : ""));

            if(item.getType() == ItemBook.TypeSection.Paragraph){
//                том.раздел.глава.параграф
//                new ItemsRange(item.getName(),"",item.getTom()+"."+item.getRazdel()+"."+item.getGlava()+"."+item.getParagraf(),item.getDescription());
                commonDao.save(new ItemsRange(item.getName(),"",item.getTom()+"."+item.getRazdel()+"."+item.getGlava()+"."+item.getParagraf(),item.getDescription()));
            }

        }

    }

    /**
     * This function read .xlsx file and return list of all
     * elements in this file, with checking type of element
     * and skip wrong strings.
     * @param in InputStream
     * @return list all Items of document in file.
     * @throws IOException
     */
    public static List<ItemBook> fillListItemBooks(InputStream in) throws IOException {
        // Using XSSF for xlsx format, for xls use HSSF
        Workbook workbook = new XSSFWorkbook(in);
        int numberOfSheets = workbook.getNumberOfSheets();
        // main part can appear multiple times in file
        boolean titul = false;
        boolean cikl = false;
        boolean root = false;
        List<ItemBook> categories = new ArrayList<>();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            //iterating over each row
            Iterator rowIterator = sheet.iterator();
            int numRow = 0;
            int tom = 0;
            String razdel = null;
            String glava = null;
            String paragraf = null;
            while (rowIterator.hasNext()) {
                ItemBook main = null;
                int numColumn = 0;
                Row row = (Row) rowIterator.next();
                Iterator cellIterator = row.cellIterator();
                String name = "";
                String code = "";
                String description = "";
                boolean rowCurrent = false;
                while (cellIterator.hasNext()) {
                    String str = cellIterator.next().toString();
                    // check for the element type
                    if (!Objects.equals(str, "") && !str.isEmpty()) {
                        if (!titul) {
                            if (str.toLowerCase().startsWith("содержание") || str.toLowerCase().startsWith("бдк")) {
                                titul = true;
                                if (str.toLowerCase().startsWith("содержание"))
                                    cikl = true;
                                continue;
                            }
                        }
                        if (titul && !root) {
                            main = new ItemBook(str.split("\\. ")[str.split("\\. ").length - 1]);
                            tom = (main.getDescription().matches("\\d+")?Integer.parseInt(main.getDescription()):0);
//                            root = true;
                            main.setType(ItemBook.TypeSection.Root);
                            main.setCikl(cikl);
                            categories.add(main);
                            main = null;
                            cikl = false;
                            root = false;
                            titul = false;
                            continue;
                        }
                        if (str.toLowerCase().startsWith("раздел ")) {
                            String[] strArray = str.split(" ");
                            ItemBook itemBook = new ItemBook(strArray[0] + " " + strArray[1]);
                            razdel = String.valueOf(RomanNumber.parse(strArray[1]));
                            for (int j = 2; j < strArray.length - 1; j++) {
                                itemBook.setName(strArray[j] + ((j < strArray.length - 2) ? " " : ""));
                            }
                            itemBook.setType(ItemBook.TypeSection.Section);
                            categories.add(itemBook);
                            continue;
                        }
                        if (str.toLowerCase().startsWith("глава ")) {
                            String[] strArray = str.split(" ");
                            glava = strArray[1];
                            ItemBook itemBook = new ItemBook(strArray[0] + " " + strArray[1]);
                            for (int j = 2; j < strArray.length - 1; j++) {
                                itemBook.setName(strArray[j] + ((j < strArray.length - 2) ? " " : ""));
                            }
                            itemBook.setType(ItemBook.TypeSection.Chapter);
                            categories.add(itemBook);
                            continue;
                        } else {
                            if (!rowCurrent) {
                                name = str;
                                rowCurrent = true;
                                continue;
                            }
                            if (description.isEmpty()) {
                                description = str;
                                continue;
                            }
                            if (code.isEmpty()) {
                                if(tom == 0) {
                                    code = str;
                                }
                                else {
                                    code = tom + "." + razdel + "." + str;
                                }
                                ItemBook itemBook = new ItemBook(description);
                                itemBook.setType(ItemBook.TypeSection.Paragraph);
                                itemBook.setName(name);
                                itemBook.setCode(code);
                                itemBook.setRazdel(razdel);
                                itemBook.setParagraf(paragraf);
                                itemBook.setGlava(glava);

                                categories.add(itemBook);
                            }
                        }
                    }
                    numColumn++;
                }
                numRow++;
            }
        }
        return categories;
    }
}