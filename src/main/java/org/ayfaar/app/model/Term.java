package org.ayfaar.app.model;

import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
//@Audited
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "ии:термин:")
public class Term extends UID {

    @Column(unique = true)
    private String name;
    private String shortDescription;
    @Column(columnDefinition = "TEXT")
    private String description;
//    private String mode; //падеж
//    private Boolean multipleMode;

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

    /*public Term(String name, String mode, boolean multipleMode) {
        this.name = name;
        this.mode = mode;
        this.multipleMode = multipleMode;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
}
