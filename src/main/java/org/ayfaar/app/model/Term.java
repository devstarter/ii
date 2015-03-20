package org.ayfaar.app.model;

import lombok.Getter;
import lombok.Setter;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
//@Audited
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "ии:термин:")
@Getter @Setter
public class Term extends UID {

    @Column(unique = true)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String shortDescription;
    @Column(columnDefinition = "TEXT")
    private String taggedShortDescription;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(columnDefinition = "TEXT")
    private String taggedDescription;


    public Term(String name) {
        this.name = name;
    }

    public Term(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Term() {
    }

    public Term(String name, String shortDescription, String description) {
        this(name, description);
        this.shortDescription = shortDescription;
    }

}
