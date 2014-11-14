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
    public List<Map.Entry<String, TermProvider>> getAll();
    public TermProvider getTermProvider(String name);
    public Term getTerm(String name);

    public interface TermProvider {
        public String getName();
        public String getUri();
        public boolean hasShortDescription();
        public TermProvider getMainTermProvider();
        public Term getTerm();
        public List<String> getMorphs();
        public List<TermProvider> getAliases();
        public List<TermProvider> getAbbreviations();
        public TermProvider getCode();
        public Byte getType();
        boolean hasMainTerm();
        boolean isAbbreviation();
        boolean isAlias();
        boolean isCode();
    }
}
