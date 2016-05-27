package topics;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class FilesExcelParser {

    public static Map<String, String> parse() {
        Map<String, String> audioRecordMap = new HashMap<>();
        InputStream in;
        HSSFWorkbook wb = null;

        try {
            in = FilesExcelParser.class.getResourceAsStream("/topics/files.xls");
            wb = new HSSFWorkbook(in);
        } catch (IOException e) {
            log.error("File is not find ", e);
        }

        HSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        List<String> list;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            list = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                int cellType = cell.getCellType();
                if (cellType == Cell.CELL_TYPE_STRING) {
                    if (columnIndex == 2) list.add(cell.getStringCellValue());
                    if (columnIndex == 3) list.add(cell.getStringCellValue());
                }
            }
            if(list.size() > 1) {
                audioRecordMap.put(list.get(0), list.get(1));
            }
        }
        return audioRecordMap;
    }
}
