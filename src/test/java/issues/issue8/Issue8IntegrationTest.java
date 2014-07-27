package issues.issue8;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.SearchController;
import org.ayfaar.app.model.ContentSearchResult;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class Issue8IntegrationTest extends IntegrationTest{
    @Inject
    private SearchController searchController;

    private List<ContentSearchResult> expectedContent;
    private List<ContentSearchResult> expectedSynonyms;

    @Before
    public void init() {
        expectedContent = new ArrayList<ContentSearchResult>();
        expectedSynonyms = new ArrayList<ContentSearchResult>();

        ContentSearchResult content0 = new ContentSearchResult();
        ContentSearchResult content1 = new ContentSearchResult();
        ContentSearchResult content12 = new ContentSearchResult();

        content0.setUri("ии:пункт:1.0003");
        content0.setName(null);
        content0.setQoute("Взять хотя бы то, что все ныне принятые Представления о мерностных свойствах Пространства-<strong>Времени</strong>" +
                            " весьма, весьма ограничены и очень далеки от более истинного Понимания Природы подобных явлений.");

        content1.setUri("ии:пункт:1.0008");
        content1.setName(null);
        content1.setQoute("Иначе во <strong>время</strong> Медитаций (о которых будет очень подробно рассказано в одной из следующих книг «Основ») " +
                "на любой из этих Звуковых Космических Кодов, в информационное пространство вашей Формы Самосознания будет резонационно " +
                "привлечена совершенно иная по Смыслу Информация, структурированная иными СФУУРММ-Формами (субъективными Представлениями).");

        content12.setUri("ии:пункт:1.0031");
        content12.setName(null);
        content12.setQoute("Принцип уникальности каждой фокусной Конфигурации, структурирующей любую из Формо-систем Миров, который реализуется" +
                " благодаря наличию у каждой Формо-системы Миров «индивидуальных» энергетической (Резомиралы) и информационной (ОДС) «составляющих»," +
                " что не позволяет в одной резонационной точке Пространства-<strong>Времени</strong> одновременно проявиться даже хотя бы двум абсолютно идентичным по" +
                " своей качественности Фокусным Динамикам.");

        expectedContent.add(content0);
        expectedContent.add(content1);
        expectedContent.add(content12);

        ContentSearchResult synonym1 = new ContentSearchResult();
        ContentSearchResult synonym2 = new ContentSearchResult();
        ContentSearchResult synonym3 = new ContentSearchResult();

        synonym1.setUri("ии:пункт:1.0031");
        synonym1.setName(null);
        synonym1.setQoute("Наличие у каждой Формо-системы Миров энергоинформационного содержимого или <strong>Памяти</strong>-Мира-О-Былом," +
                " присущего только Фокусной Динамике её Формо-Творцов.");

        synonym2.setUri("ии:пункт:1.0056");
        synonym2.setName(null);
        synonym2.setQoute("Последние между собой резонационно формируют в <strong>индивидуальных ОДС</strong> каждой из Форм Самосознаний узкоспецифические" +
                " конфигурационные сочетания - УУ-конгломераты (во ФЛУУ-ЛУУ-комплексах - ФЛУУ-дубли).");

        synonym3.setUri("ии:пункт:1.0299");
        synonym3.setName(null);
        synonym3.setQoute("Это осуществляется Формо-Творцами определённых центров головного мозга за счёт ассоциативного обобщения ими характерных признаков" +
                " каждого текущего момента Жизни (основа нашей «кратковременной <strong>памяти</strong>») и сравнительного анализа его с Опытом, который уже имеется в <strong>" +
                "индивидуальной ОДС</strong> («долговременная <strong>память</strong>»).");

        expectedSynonyms.add(synonym1);
        expectedSynonyms.add(synonym2);
        expectedSynonyms.add(synonym3);
    }

    @Test
    public void testLength() {
        String query = "время";
        Integer page = 0;
        List<ContentSearchResult> actual = searchController.searchInContent(query, page);
        assertTrue(actual.size() == 13);
    }

    @Test
    public void testEqualityUri() {
        String query = "время";
        Integer page = 0;
        List<ContentSearchResult> actual = searchController.searchInContent(query, page);
        assertEquals(actual.get(0).getUri(), "ии:пункт:1.0003");
        assertEquals(actual.get(5).getUri(), "ии:пункт:1.0018");
        assertEquals(actual.get(9).getUri(), "ии:пункт:1.0028");
        assertEquals(actual.get(12).getUri(), "ии:пункт:1.0031");
    }

    @Test
    public void testEqualityContentSearchResults() {
        String query = "время";
        Integer page = 0;
        List<ContentSearchResult> actual = searchController.searchInContent(query, page);
        assertEquals(actual.get(0), expectedContent.get(0));
        assertEquals(actual.get(1), expectedContent.get(1));
        assertEquals(actual.get(12), expectedContent.get(2));
    }

    @Test
    public void testSynonyms() {
        String query = "память";
        Integer page = 0;
        List<ContentSearchResult> actual = searchController.searchInContent(query, page);
        assertEquals(actual.get(0), expectedSynonyms.get(0));
        assertEquals(actual.get(1), expectedSynonyms.get(1));
        assertEquals(actual.get(10), expectedSynonyms.get(2));
    }
}
