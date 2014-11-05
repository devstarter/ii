package org.ayfaar.app.utils;

import org.ayfaar.app.model.Term;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NewTermsMap {
    Set<Map.Entry<String, TermProvider>> getAll();
    TermProvider getTermProvider(String name);
    byte getTermType(String name);
    TermProvider getMainTermProvider(String name);
    Term getTerm(String name);

    public interface TermProvider {
        public String getUri();
        public String getMainTermUri();
        public Term getTerm();
        public List<TermProvider> getAliasTermProviders();
        public List<TermProvider> getAbbreviationTermProviders();
        public List<TermProvider> getCodeTermProviders();
    }
}
