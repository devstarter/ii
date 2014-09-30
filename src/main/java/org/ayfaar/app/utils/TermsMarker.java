package org.ayfaar.app.utils;

import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.model.Term;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;

@Component
public class TermsMarker {
    public final static String TAG_NAME = "term";

    @Inject TermsMap termsMap;

    /**
     * Пометить все термины в тексте тегами <term></term>.
     * Например: текст до <term id="термин">термином</term> текст после
     *
     * За основу взять org.ayfaar.app.synchronization.mediawiki.TermSync#markTerms
     *
     * @param text исходный текст с терминами
     * @return текст с тегами терминов
     */
    public String mark(String text) {
        for (Map.Entry<String, Term> entry : termsMap.getAll()) {
           // ...
        }

        throw new NotImplementedException();
    }
}
