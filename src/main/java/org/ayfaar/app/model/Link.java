package org.ayfaar.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

import static org.ayfaar.app.utils.hibernate.EnumHibernateType.CLASS;
import static org.ayfaar.app.utils.hibernate.EnumHibernateType.ENUM;

@Entity
@Data
@NoArgsConstructor
/**
 * Связывает между собой две произвольные сущности наследуюющие UID
 */
public class Link {

    @Id
    @GeneratedValue
    private Integer linkId;
    @Type(type = ENUM, parameters = @org.hibernate.annotations.Parameter(name = CLASS, value = "org.ayfaar.app.model.LinkType"))
    private LinkType type;
    private String source;
    @Column(columnDefinition = "TEXT")
    private String quote;
    @Column(columnDefinition = "TEXT")
    private String taggedQuote;
    @Column(columnDefinition = "TEXT")
    private String comment;
    private Float rate;
    private Date createdAt = new Date();

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
        this(uid1, uid2, LinkType.getEnum(type));
    }

    public Link(UID uid1, UID uid2, LinkType type) {
        this(uid1, uid2);
        this.type = type;
    }

    public Link(Term term, Item item, String quote, String taggedQuote) {
        this(term, item, quote);
        this.taggedQuote = taggedQuote;
    }

    public Link(UID uid1, UID uid2, String quote) {
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.quote = quote;
    }

    public Link(UID uid1, UID uid2, LinkType type, String comment) {
        this(uid1, uid2, type);
        this.comment = comment;
    }
}
