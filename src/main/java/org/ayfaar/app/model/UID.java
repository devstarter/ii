package org.ayfaar.app.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UID {

    public static String NAME_SPACE;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UriGenerator")
    @GenericGenerator(name = "UriGenerator", strategy = "org.ayfaar.app.utils.UriGenerator")
    private String uri;

    public abstract String generateUri();

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
