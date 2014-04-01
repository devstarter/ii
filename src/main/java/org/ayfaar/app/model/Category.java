package org.ayfaar.app.model;

import lombok.NoArgsConstructor;
import org.ayfaar.app.annotations.Uri;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Audited
@PrimaryKeyJoinColumn(name="uri")
@NoArgsConstructor
@Uri(nameSpace = "категория:")
public class Category extends UID {

    @Column(unique = true)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String parent;
    private String next;
    private String start;
    private String end;

    public Category(String name, String parent) {
        this(name);
        this.parent = parent;
    }
    public Category(String name, String description, String parent) {
        this(name, parent);
        this.description = description;
    }

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
