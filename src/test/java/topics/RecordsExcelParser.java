package topics;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.Topic;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


@Slf4j
public class RecordsExcelParser {

    public static List<Record> parse() {

        List<Record> records = new ArrayList<>();
        InputStream in;
        XSSFWorkbook wb = null;

        try {
            in = RecordsExcelParser.class.getResourceAsStream("/topics/2014-06-10_1, 2005-2013.xlsx");
            wb = new XSSFWorkbook(in);
        } catch (IOException e) {
            log.error("File is not find", e);
        }

        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        List<String> list;
        boolean end = false;
        while (rowIterator.hasNext()&&!end) {
            int cellIndex = 0;
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            list = new ArrayList<>();
            Record record = new Record();

            while (cellIterator.hasNext()&&cellIndex<13) {
                cellIndex++;
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                int cellType = cell.getCellType();
                if (cell.getRowIndex()==2471) end = true;
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        String stringCellValue = cell.getStringCellValue();
                        if (columnIndex ==4)
                            record.setName(stringCellValue);
                        if (columnIndex == 12 && cell.getStringCellValue()!=null) {
                            String[] split = stringCellValue.split("[,;:.|'!?\\s]+");
                            for (String s : split) {
                                list.add(s);
                            }
                            record.setTopicCods(list);
                        }
                        break;

                    case Cell.CELL_TYPE_FORMULA:
                        if (columnIndex == 3)record.setCode(cell.getStringCellValue());
                        break;
                }
            }
            if (row.getRowNum()!=0)records.add(record);
        }
        return records;
    }
}
