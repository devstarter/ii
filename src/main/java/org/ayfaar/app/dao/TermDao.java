package org.ayfaar.app.dao;

import lombok.Data;
import org.ayfaar.app.model.Term;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TermDao extends BasicCrudDao<Term> {
    Term getByName(@NotNull String name);

    List<Term> getLike(String field, String value);

    List<Term> getGreaterThan(String field, Object value);

    List<TermInfo> getAllTermInfo();

    List<Term> getAllWithDescriptionGid();

    @Data
    class TermInfo {
        private String name;
        private boolean hasShortDescription;

        public TermInfo(String name, boolean hasShortDescription) {
            this.name = name;
            this.hasShortDescription = hasShortDescription;
        }
    }
}
