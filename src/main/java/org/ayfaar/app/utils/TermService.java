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
    Optional<TermProvider> getByUri(String uri);
    Optional<TermProvider> getMainOrThis(String name);
    Term getTerm(String name);
    void reload();

    void save(Term term);

    interface TermProvider {
        String getName();
        String getUri();
        boolean hasShortDescription();
        Optional<TermProvider> getMain();
        default TermProvider getMainOrThis() {
            return getMain().orElse(this);
        }
        Term getTerm();
        List<String> getMorphs();
        List<TermProvider> getAliases();
        List<TermProvider> getAbbreviations();
        Optional<TermProvider> getCode();
        LinkType getType();
        boolean hasMain();
        boolean isAbbreviation();
        boolean isAlias();
        boolean isCode();
        boolean hasCode();
        List<String> getAllAliasesWithAllMorphs();
        List<String> getAllAliasesAndAbbreviationsWithAllMorphs();

        Optional<String> getShortDescription();
    }
}
