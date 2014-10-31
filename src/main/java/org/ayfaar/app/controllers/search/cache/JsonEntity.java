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
    /**
     * по идее это поле нам не нужно, так как поиск в базе осуществляется по uid, я его использую просто как id
     */
    @Id
    @Column(unique = true)
    private String name;
    @OneToOne(fetch = FetchType.LAZY)
    private UID uid;
    @Column(name = "content")
    private String jsonContent;

    public JsonEntity(String name, UID uid, String jsonContent) {
        this.name = name;
        this.uid = uid;
        this.jsonContent = jsonContent;
    }
}
