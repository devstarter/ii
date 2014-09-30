package org.ayfaar.app.utils;

import org.ayfaar.app.model.Term;

import java.util.Map;
import java.util.Set;

public interface TermsMap {
    /**
     * Возвращает термины во всех падежах и соответствующие объекты, содержащие однозначные имена
     * (обычно в именительном падеже). Например "времени" => "Время", "времён" => "Время" и т. д.
     */
    Set<Map.Entry<String, Term>> getAll();
}
