package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class SuggestionsControllerIntegrationParamsTest extends IntegrationTest {
    @Inject
    SuggestionsController searchController;

    @Test
    public void testTerms() {
        String query = "гал";
        List<String> suggestions = searchController.suggestions(query,true,false,false,false,false);

        assertEquals(5, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) == 0);// Галактический Сектор
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) == 0); // Галактическая Сущность
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) == 0); // Галактический Странник
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) > 0); // Звёздно-Галактическая Форма
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) == 0); // Галактический УЛУУГУМА-Дезинтеграционный Луч АИЙ-ЙЯ
    }

    @Test
    public void testTopics() {
        String query = "тол";
        List<String> suggestions = searchController.suggestions(query,false,true,false,false,false);

        assertEquals(5, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) == 0);// Толерантность
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) > 0); // Предостережение о неправильности толкования протоформных признаков
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) > 0); // Продумать сроки и необходимость прохождения. Например, только после прохождения 1-ого курса
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) > 0); // "Всё" есть только в общей ОДС. О разделении Информации между мирами. В каждой Формо-системе есть только "своя" часть Информации
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) > 0); // Третий: самовосприятие себя как всего мира; весь мир – это я, и только я несу ответственность за всё, что происходит со мной и вокруг меня. (альтруизм, эмпатия, Интеллект)
    }

    @Test
    public void testCategories() {
        String query = "гла";
        List<String> suggestions = searchController.suggestions(query,false,false,true,false,false);

        assertEquals(5, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) == 0);// Глава 8
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) == 0); // Глава 9
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) == 0); // Глава 6
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) == 0); // Глава 10
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) == 0); // Глава 11
    }

    @Test
    public void testDocuments() {
        String query = "ос";
        List<String> suggestions = searchController.suggestions(query,false,false,false,true,false);

        assertEquals(5, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) > 0);// Мотивация как механизм работы самосознания
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) > 0); // Состояние Наблюдателя или познакомьтесь с самим собой
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) > 0); // Самоанализ психоментальных состояний (Дррааоллдлисс).pdf
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) == 0); // Осознанность – наш компас в  многомирии и ключ к гармоничной жизни (Дррааоллдлисс)
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) > 0); // Выдержки из книги Хосе Стивенса «Приручи своих Драконов. Как обратить недостатки в достоинства»
    }

    @Test
    public void testVideos() {
        String query = "рас";
        List<String> suggestions = searchController.suggestions(query,false,false,false,false,true);

        assertEquals(5, suggestions.size());
        // тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) == 0);// Распаковка Формо-Копий через ВЭН
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) > 0); // Механизм осуществления межвозрастных перефокусировок
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) > 0); // Имеет ли значение возрастной фактор при изучении Ииcссиидиологии?
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) == 0); // Рассказ автора Ииссиидиологии о том, как  у него появлялось ясновидение
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) > 0); // По-конгломератное пераспределение Конфигурации Самосознания в момент перефокусировки
    }


    @Test
    public void testTopicsWithTerms() {
        String query = "тол";
        List<String> suggestions = searchController.suggestions(query, true, true, false, false, false);

        assertEquals(10, suggestions.size());

        //тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) == 0); // Толерантность
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) > 0);// Предостережение о неправильности толкования протоформных признаков
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) > 0); // Продумать сроки и необходимость прохождения. Например, только после прохождения 1-ого курса
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) > 0); // "Всё" есть только в общей ОДС. О разделении Информации между мирами. В каждой Формо-системе есть только "своя" часть Информации
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) > 0); // Третий: самовосприятие себя как всего мира; весь мир – это я, и только я несу ответственность за всё, что происходит со мной и вокруг меня. (альтруизм, эмпатия, Интеллект)
        assertTrue(suggestions.get(5).toLowerCase().indexOf(query) < 0); // СТООЛЛ-ВВУ
        assertTrue(suggestions.get(6).toLowerCase().indexOf(query) < 0); // УУРТТ-ООЛКК
        assertTrue(suggestions.get(7).toLowerCase().indexOf(query) < 0); // стооллмиизм
        assertTrue(suggestions.get(8).toLowerCase().indexOf(query) < 0); // ТОО-ЛТ-УУ-ЙФ
        assertTrue(suggestions.get(9).toLowerCase().indexOf(query) < 0); // СТООЛЛМИИ-СВУУ
        System.out.println("");

    }

    @Test
    public void testAllItems() {
        String query = "ва";
        List<String> suggestions = searchController.suggestions(query, true, true, true, true, true);

        assertEquals(22, suggestions.size());

        //тест последовательности
        assertTrue(suggestions.get(0).toLowerCase().indexOf(query) == 0);//        Карма. Кармические ваимосвязи
        assertTrue(suggestions.get(1).toLowerCase().indexOf(query) > 0);//        Самое важное - это внутренние Состояния
        assertTrue(suggestions.get(2).toLowerCase().indexOf(query) > 0);//        Варианты ближайшего будущего зависят от активности уровней самосознания
        assertTrue(suggestions.get(3).toLowerCase().indexOf(query) == 0);//        На сколько мы себя знаем, понимаем и умеем прогнозировать своё поведение? Что такое эмоции и в какой степени мы осознаём их? О важности умения распознавать свои эмоции и психические состояния
        assertTrue(suggestions.get(4).toLowerCase().indexOf(query) > 0);//        Глава 8
        assertTrue(suggestions.get(5).toLowerCase().indexOf(query) > 0);//        Глава 16
        assertTrue(suggestions.get(6).toLowerCase().indexOf(query) > 0);//        Глава 15
        assertTrue(suggestions.get(7).toLowerCase().indexOf(query) > 0);//        Глава 14
        assertTrue(suggestions.get(8).toLowerCase().indexOf(query) > 0);//        Глава 13
        assertTrue(suggestions.get(9).toLowerCase().indexOf(query) > 0);//        Мотивация как механизм работы самосознания
        assertTrue(suggestions.get(10).toLowerCase().indexOf(query) > 0);//        Выдержки из книги Хосе Стивенса «Приручи своих Драконов. Как обратить недостатки в достоинства»
        assertTrue(suggestions.get(11).toLowerCase().indexOf(query) > 0);//        Самосознание. Часть 3. Принцип формирования «ротационного Цикла»
        assertTrue(suggestions.get(12).toLowerCase().indexOf(query) > 0);//        Самое важное - это внутренние Состояния, на которых мы совершаем выборы
        assertTrue(suggestions.get(13).toLowerCase().indexOf(query) > 0);//        Откуда берутся самые первые Формо-Копии, как они накладываются на  наше Восприятие
        assertTrue(suggestions.get(14).toLowerCase().indexOf(query) > 0);//        Трансформировав свои представления, мы будем осознавать себя живущими в новых Мирах
        assertTrue(suggestions.get(15).toLowerCase().indexOf(query) > 0);//        Как можно скорректировать технократический, рационалистический путь развития человечества
        assertTrue(suggestions.get(17).toLowerCase().indexOf(query) == 0);//        Вакуум
        assertTrue(suggestions.get(18).toLowerCase().indexOf(query) > 0); //        Виваксы
        assertTrue(suggestions.get(19).toLowerCase().indexOf(query) == 0);//        Вариативность
        assertTrue(suggestions.get(20).toLowerCase().indexOf(query) == 0);//        ВААЛЛ-ВАА-ККАА
        assertTrue(suggestions.get(21).toLowerCase().indexOf(query) > 0);//        ВСЕ-Пустотность-ВСЕ-Вакуумность


    }
}
