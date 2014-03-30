package org.ayfaar.app.model;

import org.ayfaar.app.annotations.Uri;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Audited
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "ии:термин:")
public class Term extends UID {

    @Column(unique = true)
    private String name;
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
}
