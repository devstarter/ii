package topics;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;

public class WordParser {

    public static String parseWord(String url) throws IOException {

        HWPFDocument doc=new HWPFDocument(WordParser.class.getResourceAsStream("/topics/" + url));
        WordExtractor we = new WordExtractor(doc);
        return we.getText();
    }
    public static String parseWordX(String url) throws IOException {

        XWPFDocument docx = new XWPFDocument(WordParser.class.getResourceAsStream("/topics/" + url));
        XWPFWordExtractor we = new XWPFWordExtractor(docx);
        return we.getText();
    }
}
