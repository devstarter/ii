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
        Topic topic;
        InputStream in;
        XSSFWorkbook wb = null;
        int emptyCount = 0;

        try {
            in = TopicsExcelParser.class.getResourceAsStream("/topics/Классификатор методики МИЦИАР.xlsx"); //from resource dir
            wb = new XSSFWorkbook(in);
        } catch (IOException e) {
            log.error("File is not find", e);
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
                        if (cellIndex < 2)
                            list.add(cell.getStringCellValue());
                        cellIndex++;
                        break;
                    default:
                        if (cellIndex == 1) {
                            list.add("EMPTY" + emptyCount); //if key is empty, add "EMPTY", если не нужно заменить " "
                            emptyCount++;
                            cellIndex++;
                        }
                        break;
                }
            }
            if (list.size() >= 1) {
                codeTopicMap.put(list.get(1), list.get(0));
            }
        }
        return codeTopicMap;
    }
}
