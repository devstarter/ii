package org.ayfaar.app.controllers.search.cache;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.ayfaar.app.model.UID;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name= "cache")
public class JsonEntity {
    @Id
    @Column(unique = true)
    private String name;
    @OneToOne
    private UID uri;
    @Column(name = "content")
    private String jsonContent;

    public JsonEntity(String name, UID uri, String jsonContent) {
        this.name = name;
        this.uri = uri;
        this.jsonContent = jsonContent;
    }
}
