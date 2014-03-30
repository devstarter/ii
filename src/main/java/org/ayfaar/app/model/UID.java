package org.ayfaar.app.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UID {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UriGenerator")
    @GenericGenerator(name = "UriGenerator", strategy = "org.ayfaar.app.utils.UriGenerator")
    private String uri;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UID uid = (UID) o;
        return uri.equals(uid.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
