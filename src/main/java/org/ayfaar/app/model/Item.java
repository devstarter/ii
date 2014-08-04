package org.ayfaar.app.model;

import org.apache.lucene.search.FieldCache;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Comparator;

@Entity
@PrimaryKeyJoinColumn(name="uri")
//@Audited
@Uri(nameSpace = "ии:пункт:", field = "number")
public class Item extends UID implements Comparable<Item>{

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

    public static boolean isItemNumber(String s) {
        return s.matches("^\\d\\d?\\.\\d{4}\\d?$");
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

    @Override
    public int compareTo(Item that) {
        double itemNumber1 = Double.parseDouble(this.getNumber());
        double itemNumber2 = Double.parseDouble(that.getNumber());

        return (itemNumber1 == itemNumber2) ? 0 : (itemNumber1 > itemNumber2) ? 1 : -1;

    }
}
