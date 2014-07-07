package org.ayfaar.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTermUri() {
        return termUri;
    }

    public void setTermUri(String termUri) {
        this.termUri = termUri;
    }

}
