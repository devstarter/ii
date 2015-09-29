package org.ayfaar.app.utils;

import org.ayfaar.app.model.Term;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс взаимодействия с предварительно загруженными всеми терминами
 */
public interface TermsMap {
    /**
     * Возвращает термины во всех падежах и соответствующие объекты, содержащие однозначные имена
     * (обычно в именительном падеже). Например "времени" => "Время", "времён" => "Время" и т. д.
     */
    List<Map.Entry<String, TermProvider>> getAll();
    TermProvider getTermProvider(String name);
    Term getTerm(String name);
    void reload();

    interface TermProvider {
        String getName();
        String getUri();
        boolean hasShortDescription();
        TermProvider getMainTermProvider();
        Term getTerm();
        List<String> getMorphs();
        List<TermProvider> getAliases();
        List<TermProvider> getAbbreviations();
        TermProvider getCode();
        Byte getType();
        boolean hasMainTerm();
        boolean isAbbreviation();
        boolean isAlias();
        boolean isCode();
        boolean hasCode();
    }
}
