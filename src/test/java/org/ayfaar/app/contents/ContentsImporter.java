package org.ayfaar.app.contents;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ayfaar.app.Application;
import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.ItemsRange;
import org.ayfaar.app.utils.RomanNumber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("dev")
@WebAppConfiguration
@SpringApplicationConfiguration(Application.class)
public class ContentsImporter {

    @Inject CommonDao commonDao;

    @Test
    public void main() throws IOException, XLSParsingException {

        InputStream in = new FileInputStream("./src/test/resources/content/5tom-content.xlsx");

        List<ItemBook> list = fillListItemBooks(in);
        System.out.println();
        List<ItemsRange> itemsRanges = getItemsRange(list); //получили готовый массив ItemsRange
        System.out.println();
        in.close();
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
                itemBook.setTitle(sheet.getRow(1).getCell(1).toString());
            }else{
                throw new IncorrectRowException("Ячейка \"B2\" должна содержать название тома");
            }
        } else{
            throw new IncorrectRowException("Ячейка \"A1\" должна содержать \"Том {Номер тома}\"");
        }
        return itemBook;
    }

    //парсим xlsx
    private static List<ItemBook> fillListItemBooks(InputStream in) throws IOException, XLSParsingException {
        // Using XSSF for xlsx format, for xls use HSSF
        Workbook workbook = new XSSFWorkbook(in);
        int numberOfSheets = workbook.getNumberOfSheets();

        List<ItemBook> categories = new ArrayList<>();
        //создаем список со всеми категориями
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);

            //на листе только один том, при этом номер в 1-й строке, название во 2-й строке
            categories.add(getTom(sheet)); //добавили том

            for (Row row : sheet) {
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
                        throw new IncorrectRowException("Неверная строка №"+(row.getRowNum() + 1) + " для параграфа, не заполнено либо название, либо номер абзаца");
                    }
                    itemBook.setCategoryNumber(cell_0.toString().trim());
                    itemBook.setType(SectionType.Paragraph);
                    itemBook.setTitle(cell_1.toString().trim());
                    itemBook.setItemNumber(cell_2.toString().trim());
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
                        String numberOfSection = str.substring("раздел ".length(),newLine).trim();
                        itemBook.setCategoryNumber(numberOfSection);
                        itemBook.setTitle(str.substring(newLine + 1, str.length()).trim());
                        itemBook.setType(SectionType.Section);
                    } else if(str.toLowerCase().startsWith("глава ")){
                        int dot = str.indexOf(".");
                        String numberOfChapter = str.substring("глава ".length(),dot).trim();
                        itemBook.setCategoryNumber(numberOfChapter);
                        itemBook.setTitle(str.substring(dot + 1, str.length()).trim());
                        itemBook.setType(SectionType.Chapter);
                    }else {
                        throw new IncorrectRowException("Неверная строка №" + (row.getRowNum() + 1) + ". Данная строка не являесят ни Томом, ни разделом, ни главой, ни параграфом");
                    }
                }
                categories.add(itemBook);
            }
        }
        return categories;
    }

    private static List<ItemsRange> getItemsRange(List<ItemBook> itemBookList) throws XLSParsingException{
        List<ItemsRange> itemsRanges = new ArrayList<>(itemBookList.size());//чтобы не тратить время на увеличение массива
        String currentTom  = "", currentSection = "", currentChapter = "";
        for (int i = 0; i < itemBookList.size() - 1; i++) { // вычли единицу потому что не понятно как для последнего абзаца считать диапазон
            switch (itemBookList.get(i).getType()){
                case Tom:
                    currentTom = itemBookList.get(i).getCategoryNumber();
                    break;
                case Section:
                    currentSection = String.valueOf(RomanNumber.parse(itemBookList.get(i).getCategoryNumber()));
                    break;
                case Chapter:
                    currentChapter = itemBookList.get(i).getCategoryNumber();
                    break;
                case Paragraph:
                    int k = getStep(itemBookList, i);
                    String lastItem = ItemController.getPrev(itemBookList.get(k).getItemNumber());
                    String code;
                    if(!(currentTom.isEmpty() || currentSection.isEmpty() || currentChapter.isEmpty())){
                        code = currentTom + "." + currentSection + "." + itemBookList.get(i).getCategoryNumber();
                    }else {
                        //Том (currentTom) не может быть пустым, потому что словили бы исключение в методе fillListItemBooks.
                        throw new IncorrectSheetException("Лист с томом №" + currentTom + "неверной структуры. Для параграфа №"
                            + itemBookList.get(i).getItemNumber() +" нет либо раздела, либо главы.");
                    }

                    ItemsRange itemsRange = new ItemsRange(itemBookList.get(i).getItemNumber(),
                                                            lastItem,
                                                            code,
                                                            itemBookList.get(i).getTitle());
                    itemsRanges.add(itemsRange);
                    break;
            }
        }
        return itemsRanges;
    }

    private static int getStep(List<ItemBook> itemBookList, int i) throws XLSParsingException{
        int k = i + 1;
        if (k < itemBookList.size()){
            if(itemBookList.get(k).getType() != SectionType.Paragraph){
                k = getStep(itemBookList,k); //если следующее значение тоже НЕ параграф, тогда рекурсивно проверяем "следующее-следующее" значение
            }
        }else{
            throw new IncorrectSheetException("Лист с томом №" + itemBookList.get(0).getCategoryNumber() + "неверной структуры. Для параграфа №"
                    + itemBookList.get(k).getItemNumber() +" нет конечного диапазона абзаца.");
        }
        return k;
    }
}