package org.ayfaar.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Link {
    /**
     * Синомим, первым следует указывать более точное понятие или код
     */
    public static final Byte ALIAS = 1;

    /**
     * Аббревиатура или сокращение, первым указывают полное значение
     */
    public static final Byte ABBREVIATION = 2;

    /**
     * Ссылка на код понятия
     * Первый понятие, второй код
     */
    public static final Byte CODE = 4;

    /**
     * Ссылка на дочерний объет
     * Первый родитель, второй потомок
     */
    public static final Byte CHILDREN = 5;

    public static final Byte TRANSLATION = 6;

    @Id
    @GeneratedValue
    private Integer linkId;
    private Byte type;
    private String source;
    @Column(columnDefinition = "TEXT")
    private String quote;
    @Column(columnDefinition = "TEXT")
    private String taggedQuote;
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
}
