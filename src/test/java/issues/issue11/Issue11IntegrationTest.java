package issues.issue11;

import org.apache.commons.io.IOUtils;


import org.apache.http.entity.mime.content.StringBody;
import org.ayfaar.app.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


public class Issue11IntegrationTest extends IntegrationTest {

    // 1. Найти все пункты с символами: *, †, ‡, § их не должно быть

    public String getFile(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName));
    }


   // @Test
    public String cleanStr(String str) {

        List<String> notContain = new ArrayList<String>();
        notContain.add("*");
        notContain.add("†");
        notContain.add("‡");
        notContain.add("§");

        for (int i = 0; i < notContain.size(); i++) {
            str = StringUtils.replace(str, notContain.get(i), "");
        }

       return str;

    }


    // 2. Метод для очистки базы данных
    @Test
 //   @Ignore
    public void cleanDB() throws IOException{

        String cleanItem11_13017 = getFile("clean-item-11.13017.txt");
        String dirtyItem11_13017 = getFile("dirty-item-11.13017.txt");

        System.out.println(cleanStr(dirtyItem11_13017));

        // Этот медод запускается единажды для выполнения очистки всех пунктов
    }
}
