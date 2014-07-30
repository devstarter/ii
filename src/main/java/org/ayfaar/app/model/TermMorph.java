package org.ayfaar.app.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class TermMorph {
    @Id
    private String name;
    private String termUri;

    public TermMorph(String name, String termUri) {
        this.name = name;
        this.termUri = termUri;
    }

    public TermMorph() {
    }
}
