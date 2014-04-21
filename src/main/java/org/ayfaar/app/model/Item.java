package org.ayfaar.app.model;

import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="uri")
//@Audited
@Uri(nameSpace = "ии:пункт:", field = "number")
public class Item extends UID {

    @Column(unique = true)
    private String number;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String wiki;
    private String next;

    public Item(String number, String content) {
        this.number = number;
        this.content = content;
    }

    public Item(String number) {
        this.number = number;
    }

    public Item() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getWiki() {
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }
}
