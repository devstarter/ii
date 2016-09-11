package org.ayfaar.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

import static org.ayfaar.app.utils.hibernate.EnumHibernateType.CLASS;
import static org.ayfaar.app.utils.hibernate.EnumHibernateType.ENUM;

@Entity
@Data
@NoArgsConstructor
public class LightLink {

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
    private Integer createdBy;

    private String uid1;
    private String uid2;

    public static LightLink fromLink(Link l) {
        final LightLink ll = new LightLink();
        ll.setLinkId(l.getLinkId());
        ll.setType(l.getType());
        ll.setSource(l.getSource());
        ll.setQuote(l.getQuote());
        ll.setTaggedQuote(l.getTaggedQuote());
        ll.setComment(l.getComment());
        ll.setRate(l.getRate());
        ll.setCreatedAt(l.getCreatedAt());
        ll.setCreatedBy(l.getCreatedBy());
        ll.setUid1(l.getUid1().getUri());
        ll.setUid2(l.getUid2().getUri());
        return ll;
    }
}
