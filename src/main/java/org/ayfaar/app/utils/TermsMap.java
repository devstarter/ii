package org.ayfaar.app.utils;

import lombok.Data;
import org.ayfaar.app.model.Term;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс взаимодействия с предварительно загруженными всеми терминами
 */
public interface TermsMap {
    /**
     * Возвращает термины во всех падежах и соответствующие объекты, содержащие однозначные имена
     * (обычно в именительном падеже). Например "времени" => "Время", "времён" => "Время" и т. д.
     */
    public Set<Map.Entry<String, Term>> getAll();

    public Set<Map.Entry<String, TermProvider>> getAllProviders();
    public TermProvider getTermProvider(String name);
    public TermProvider getMainTermProvider(String name);
    public Term getTerm(String name);
    public byte getTermType(String name);

    public List<TermProvider> getAliases(String uri);
    public List<TermProvider> getAbbreviations(String uri);
    public List<TermProvider> getCodes(String uri);

    @Data
    public static class TermProvider {
        private String uri;
        private String mainTermUri;
        private boolean hasShortDescription;

        public TermProvider(String uri, String mainTermUri, boolean hasShortDescription) {
            this.uri = uri;
            this.mainTermUri = mainTermUri;
            this.hasShortDescription = hasShortDescription;
        }
    }
}
