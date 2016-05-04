package org.ayfaar.app.utils;

import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.Term;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс взаимодействия с предварительно загруженными всеми терминами
 */
public interface TermService {
    /**
     * Возвращает термины во всех падежах и соответствующие объекты, содержащие однозначные имена
     * (обычно в именительном падеже). Например "времени" => "Время", "времён" => "Время" и т. д.
     */
    List<Map.Entry<String, TermProvider>> getAll();
    List<TermDao.TermInfo> getAllInfoTerms();
    Optional<TermProvider> get(String name);
    Optional<TermProvider> getMainOrThis(String name);
    Term getTerm(String name);
    void reload();

    interface TermProvider {
        String getName();
        String getUri();
        boolean hasShortDescription();
        Optional<TermProvider> getMainTerm();
        Term getTerm();
        List<String> getMorphs();
        List<TermProvider> getAliases();
        List<TermProvider> getAbbreviations();
        TermProvider getCode();
        LinkType getType();
        boolean hasMainTerm();
        boolean isAbbreviation();
        boolean isAlias();
        boolean isCode();
        boolean hasCode();
        List<String> getAllAliasesWithAllMorphs();
        List<String> getAllAliasesAndAbbreviationsWithAllMorphs();
    }
}
