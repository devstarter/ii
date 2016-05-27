package topics;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ayfaar.app.model.Topic;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class TopicsExcelParser {

    public static Map<String, String> parse() {
        Map<String, String> codeTopicMap = new HashMap<>();
        InputStream in;
        XSSFWorkbook wb = null;

        try {
            in = TopicsExcelParser.class.getResourceAsStream("/topics/Классификатор методики МИЦИАР.xlsx");
            wb = new XSSFWorkbook(in);
        } catch (IOException e) {
            log.error("File is not find ", e);
        }

        XSSFSheet sheet = wb.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        List<String> list;
        while (rowIterator.hasNext()) {
            int cellIndex = 0;
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.iterator();
            list = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int cellType = cell.getCellType();
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        if (cellIndex < 2){
                            String stringCellValue = cell.getStringCellValue();
                            list.add(stringCellValue);}
                        cellIndex++;
                        break;
                    default:
                        if (cellIndex == 1) {
                            list.clear();
                            cellIndex++;
                        }
                        break;
                }
            }
            if (list.size() >= 1 && list.get(1).length()==2) {
                codeTopicMap.put(list.get(1), list.get(0));
            }
        }
        return codeTopicMap;
    }
}
