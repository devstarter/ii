package org.ayfaar.app.model;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Audited
public class Item extends UID {

    public static final String NAME_SPACE = "ии:пункт:";

    @Column(unique = true)
    private String number;
    @Column(columnDefinition = "TEXT")
    private String content;
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

    @Override
    public String generateUri() {
        return NAME_SPACE+number;
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
}
