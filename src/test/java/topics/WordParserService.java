package topics;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WordParserService {


    public static Map<String, String> parse(File folder) throws Exception {

        File[] files = folder.listFiles();
        Map<String,String> codeWithText = new HashMap<>();
        for (File file : files) {
            String url = file.getName();
            if (url.endsWith(".doc")) {
                String text = WordParser.parseWord(url);
                String code = url.substring(0, url.length() - 4);
                log.info("Parse " + url);
                codeWithText.put(code,text);

            }else if (url.endsWith(".docx")) {
                String text = WordParser.parseWordX(url);
                String code = url.substring(0, url.length() - 5);
                log.info("Parse " + url);
                codeWithText.put(code,text);
            }
        }
        return codeWithText;
    }
}
