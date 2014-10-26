package org.ayfaar.app.controllers.search.cache;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.ayfaar.app.model.UID;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@NoArgsConstructor
@Entity
public class JsonEntity {
    @Column
    private String name;
    @Column
    private String uri;
    @Column(columnDefinition = "TEXT")
    private String jsonContent;

    public JsonEntity(String name, String uri, String jsonContent) {
        this.name = name;
        this.uri = uri;
        this.jsonContent = jsonContent;
    }
}
