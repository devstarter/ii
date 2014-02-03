package org.ayfaar.app.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class Link {
    public static final Byte ALIAS = 1;
    public static final Byte ABBREVIATION = 2;
    public static final Byte DEFINITION = 3;
//    public static final byte DIRECTION = 2;
//    public static final byte PRIORITY = 3;
//    public static final byte MORPHEME_WEIGHT = -1;
//    public static final byte ABBREVIATION_WEIGHT = 1;

    @Id
    @GeneratedValue
    private Integer linkId;
    private Byte type;
    private Byte weight;
    private String note;
    @Column(columnDefinition = "TEXT")
    private String quote;

    @ManyToOne
    private UID uid1;

    @ManyToOne
    private UID uid2;

    public Link(UID uid1, UID uid2) {
        if (uid1.getUri().equals(uid2.getUri())) {
            throw new RuntimeException("Link to same URI");
        }
        this.uid1 = uid1;
        this.uid2 = uid2;
    }

    public Link(UID uid1, UID uid2, Byte type) {
        this(uid1, uid2);
        this.type = type;
    }

    public Link(UID uid1, UID uid2, Byte type, Byte weight) {
        this(uid1, uid2, type);
        this.weight = weight;
    }

    public Link() {
    }

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Byte getWeight() {
        return weight;
    }

    public void setWeight(Byte weight) {
        this.weight = weight;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public UID getUid1() {
        return uid1;
    }

    public void setUid1(UID uid1) {
        this.uid1 = uid1;
    }

    public UID getUid2() {
        return uid2;
    }

    public void setUid2(UID uid2) {
        this.uid2 = uid2;
    }
}
