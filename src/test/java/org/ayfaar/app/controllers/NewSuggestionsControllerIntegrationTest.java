package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.junit.Test;
import javax.inject.Inject;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NewSuggestionsControllerIntegrationTest extends IntegrationTest {
    @Inject NewSuggestionsController suggestionsControllerNew;

    @Test
    public void testTerms() {
        String query = "гал";
       Map<String, String> suggestions = suggestionsControllerNew.suggestions(query,true,false,false,false,false);
        assertEquals(5, suggestions.size());
        // тест последовательности
        Object[] items = suggestions.values().toArray();
        assertTrue(items[0].toString().toLowerCase().indexOf(query) == 0);// Галактический Сектор
        assertTrue(items[1].toString().toLowerCase().indexOf(query) == 0); // Галактическая Сущность
        assertTrue(items[2].toString().toLowerCase().indexOf(query) == 0); // Галактический Странник
        assertTrue(items[3].toString().toLowerCase().indexOf(query) > 0); // Звёздно-Галактическая Форма
        assertTrue(items[4].toString().toLowerCase().indexOf(query) == 0); // Галактический УЛУУГУМА-Дезинтеграционный Луч АИЙ-ЙЯ
    }

    @Test
    public void testTopics() {
        String query = "тол";
        Map<String, String> suggestions = suggestionsControllerNew.suggestions(query,false,true,false,false,false);
        assertEquals(5, suggestions.size());
        Object[] items = suggestions.values().toArray();
        // тест последовательности
        assertTrue(items[0].toString().toLowerCase().indexOf(query) == 0);// Толерантность
        assertTrue(items[1].toString().toLowerCase().indexOf(query) > 0); // Предостережение о неправильности толкования протоформных признаков
        assertTrue(items[2].toString().toLowerCase().indexOf(query) > 0); // Продумать сроки и необходимость прохождения. Например, только после прохождения 1-ого курса
        assertTrue(items[3].toString().toLowerCase().indexOf(query) > 0); // "Всё" есть только в общей ОДС. О разделении Информации между мирами. В каждой Формо-системе есть только "своя" часть Информации
        assertTrue(items[4].toString().toLowerCase().indexOf(query) > 0); // Третий: самовосприятие себя как всего мира; весь мир – это я, и только я несу ответственность за всё, что происходит со мной и вокруг меня. (альтруизм, эмпатия, Интеллект)
    }

    @Test
    public void testCategories() {
        String query = "гла";
        Map<String, String> suggestions = suggestionsControllerNew.suggestions(query,false,false,true,false,false);
        assertEquals(5, suggestions.size());
        Object[] items = suggestions.values().toArray();
        // тест последовательности
        assertTrue(items[0].toString().toLowerCase().indexOf(query) > 0);// Основы / Раздел IV / Глава 3
        assertTrue(items[1].toString().toLowerCase().indexOf(query) > 0); // Основы / Раздел IV / Глава 2
        assertTrue(items[2].toString().toLowerCase().indexOf(query) > 0); // Основы / Раздел IV / Глава 5
        assertTrue(items[3].toString().toLowerCase().indexOf(query) > 0); // Основы / Раздел IV / Глава 4
        assertTrue(items[4].toString().toLowerCase().indexOf(query) > 0); // Основы / Раздел III / Глава 1
    }

    @Test
    public void testDocuments() {
        String query = "ос";
        Map<String, String> suggestions = suggestionsControllerNew.suggestions(query,false,false,false,true,false);
        assertEquals(4, suggestions.size());
        Object[] items = suggestions.values().toArray();
        // тест последовательности
        assertTrue(items[0].toString().toLowerCase().indexOf(query) > 0);// Мотивация как механизм работы самосознания
        assertTrue(items[1].toString().toLowerCase().indexOf(query) > 0); // Состояние Наблюдателя или познакомьтесь с самим собой
        assertTrue(items[2].toString().toLowerCase().indexOf(query) == 0); // Осознанность – наш компас в  многомирии и ключ к гармоничной жизни (Дррааоллдлисс)
        assertTrue(items[3].toString().toLowerCase().indexOf(query) > 0); // Выдержки из книги Хосе Стивенса «Приручи своих Драконов. Как обратить недостатки в достоинства»
    }

    @Test
    public void testVideos() {
        String query = "рас";
        Map<String, String> suggestions = suggestionsControllerNew.suggestions(query,false,false,false,false,true);
        assertEquals(5, suggestions.size());
        Object[] items = suggestions.values().toArray();
        // тест последовательности
        assertTrue(items[0].toString().toLowerCase().indexOf(query) == 0);// Распаковка Формо-Копий через ВЭН
        assertTrue(items[1].toString().toLowerCase().indexOf(query) > 0); // Механизм осуществления межвозрастных перефокусировок
        assertTrue(items[2].toString().toLowerCase().indexOf(query) > 0); // Имеет ли значение возрастной фактор при изучении Ииcссиидиологии?
        assertTrue(items[3].toString().toLowerCase().indexOf(query) == 0); // Рассказ автора Ииссиидиологии о том, как  у него появлялось ясновидение
        assertTrue(items[4].toString().toLowerCase().indexOf(query) > 0); // По-конгломератное пераспределение Конфигурации Самосознания в момент перефокусировки
    }


    @Test
    public void testTopicsWithTerms() {
        String query = "тол";
        Map<String, String> suggestions = suggestionsControllerNew.suggestions(query,true,true,false,false,false);
        assertEquals(10, suggestions.size());
        Object[] items = suggestions.values().toArray();
        //тест последовательности
        assertTrue(items[0].toString().toLowerCase().indexOf(query) < 0); // УУРТТ-ООЛКК
        assertTrue(items[1].toString().toLowerCase().indexOf(query) < 0); // ТОО-ЛТ-УУ-ЙФ
        assertTrue(items[2].toString().toLowerCase().indexOf(query) < 0); // СТООЛЛМИИ-СВУУ
        assertTrue(items[3].toString().toLowerCase().indexOf(query) < 0); // СТООЛЛМИИ-СВУУ-ВВУ
        assertTrue(items[4].toString().toLowerCase().indexOf(query) < 0); // СТООЛЛ-ВВУ-ФЛАКС-Формо-Творец
        assertTrue(items[5].toString().toLowerCase().indexOf(query) == 0); // Толерантность
        assertTrue(items[6].toString().toLowerCase().indexOf(query) > 0);// Предостережение о неправильности толкования протоформных признаков
        assertTrue(items[7].toString().toLowerCase().indexOf(query) > 0); // Продумать сроки и необходимость прохождения. Например, только после прохождения 1-ого курса
        assertTrue(items[8].toString().toLowerCase().indexOf(query) > 0); // "Всё" есть только в общей ОДС. О разделении Информации между мирами. В каждой Формо-системе есть только "своя" часть Информации
        assertTrue(items[9].toString().toLowerCase().indexOf(query) > 0); // Третий: самовосприятие себя как всего мира; весь мир – это я, и только я несу ответственность за всё, что происходит со мной и вокруг меня. (альтруизм, эмпатия, Интеллект)
    }

    @Test
    public void testAllItems() {
        String query = "ва";
        Map<String, String> suggestions = suggestionsControllerNew.suggestions(query,true, true, true, true, true);
        assertEquals(21, suggestions.size());
        Object[] items = suggestions.values().toArray();
        //тест последовательности
        assertTrue(items[0].toString().toLowerCase().indexOf(query) == 0);//        Вакуум
        assertTrue(items[1].toString().toLowerCase().indexOf(query) == 0);//        Вариативность
        assertTrue(items[2].toString().toLowerCase().indexOf(query) == 0);//        ВААЛЛ-ВАА-ККАА
        assertTrue(items[3].toString().toLowerCase().indexOf(query) > 0);//        ВСЕ-Пустотность-ВСЕ-Вакуумность

        assertTrue(items[4].toString().toLowerCase().indexOf(query) == 0);//        Важность интереса
        assertTrue(items[5].toString().toLowerCase().indexOf(query) > 0);//        Самое важное - это внутренние Состояния
        assertTrue(items[6].toString().toLowerCase().indexOf(query) > 0);//        Алкогольная зависимость и важность интереса
        assertTrue(items[7].toString().toLowerCase().indexOf(query) == 0);//        Варианты ближайшего будущего зависят от активности уровней самосознания
        assertTrue(items[8].toString().toLowerCase().indexOf(query) > 0);//        На сколько мы себя знаем, понимаем и умеем прогнозировать своё поведение? Что такое эмоции и в какой степени мы осознаём их? О важности умения распознавать свои эмоции и психические состояния

        assertTrue(items[9].toString().toLowerCase().indexOf(query) > 0);//        БДК/Раздел III/Глава 1
        assertTrue(items[10].toString().toLowerCase().indexOf(query) > 0);//        БДК/Раздел III/Глава 4
        assertTrue(items[11].toString().toLowerCase().indexOf(query) > 0);//        БДК/Раздел III/Глава 5
        assertTrue(items[12].toString().toLowerCase().indexOf(query) > 0);//        Основы/Раздел IX/Глава 9
        assertTrue(items[13].toString().toLowerCase().indexOf(query) > 0);//        Основы/Раздел IX/Глава 8

        assertTrue(items[14].toString().toLowerCase().indexOf(query) > 0);//        Мотивация как механизм работы самосознания
        assertTrue(items[15].toString().toLowerCase().indexOf(query) > 0);//        Выдержки из книги Хосе Стивенса «Приручи своих Драконов. Как обратить недостатки в достоинства»

        assertTrue(items[16].toString().toLowerCase().indexOf(query) > 0);//        Инволюции в нашем развитии не бывает
        assertTrue(items[17].toString().toLowerCase().indexOf(query) > 0);//        Самосознание. Часть 3. Принцип формирования «ротационного Цикла»
        assertTrue(items[18].toString().toLowerCase().indexOf(query) > 0);//        Самое важное - это внутренние Состояния, на которых мы совершаем выборы
        assertTrue(items[19].toString().toLowerCase().indexOf(query) > 0);//        Основы Ииссиидиологии. Что такое Энерго-Плазма. Образование Времени и Пространства
        assertTrue(items[20].toString().toLowerCase().indexOf(query) > 0);//        Как всё появилось. Часть 3. Ирркогликтивный Импульс и образование типов бирвуляртности

    }
}
