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




    // 2. Метод для очистки базы данных
    @Test
 //   @Ignore
    public void cleanDB() throws IOException{


        // Этот медод запускается единажды для выполнения очистки всех пунктов
    }
}
