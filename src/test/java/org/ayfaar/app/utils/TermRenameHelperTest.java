package org.ayfaar.app.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

public class TermRenameHelperTest {

    @Test
    /**
     * Этап 1
     * Метод должен уметь разпознать в сообщении команды для перевода первой букы термина в нижний регистр.
     */
    public void tier1() {
        Stream.of("Ммааллссм: термин пишется с маленькой буквы", "термин пишется с маленькой буквы", "с маленькой буквы", "с маленькой")
                .forEach(message -> {
                    final Optional<TermRenameHelper.RenameSuggestion> renameSuggestion = TermRenameHelper.suggestRename("Любой термин", message);
                    Assert.assertTrue("Метод должен вернуть предложение на переименование термина и почищенное сообщение", renameSuggestion.isPresent());
                    Assert.assertEquals("любой термин", renameSuggestion.get().name);
                    Assert.assertTrue(renameSuggestion.get().message == null);
                });

    }

    @Test
    /**
     * Этап 2
     * Метод должен уметь разпознать в тексте какую часть термина нужно сделать с маленкой буквы.
     */
    public void tier12() {
        String term = "Амплификационный организационно-направляющий Импульс";
        String message = "Ммааллссм: эгллеролифтивный Элемент Творчества (словарь 3 тома) Амплификационный пишется с маленькой буквы";
        Optional<TermRenameHelper.RenameSuggestion> renameSuggestion = TermRenameHelper.suggestRename(term, message);

        Assert.assertTrue("Метод должен вернуть предложение на переименование термина и почищенное сообщение", renameSuggestion.isPresent());
        Assert.assertEquals("амплификационный организационно-направляющий Импульс", renameSuggestion.get().name);
        Assert.assertEquals("Ммааллссм: эгллеролифтивный Элемент Творчества (словарь 3 тома)", renameSuggestion.get().message);
    }

    /* Добавить тесты по аналогии для:
     * `Ммааллссм: один из типов субъективных Реальностей 1-2- и 2-3-мерных диапазонов (2.0892) термин пишется маленькими буквами`
     * для термина `Временная эфирная наполняющая` -> `временная - с маленькой буквы`
     * термин: `Габитуально`, сообщение: `габитуально`
     * термин: `ГИПЕРСОЗНАНИЕ`, сообщение: `Гиперсознание`
    */
}