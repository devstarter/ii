package org.ayfaar.app.controllers.search.cache;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@NoArgsConstructor
//@Entity
public class JsonEntity {
    @Column
    @Id
    // это ключ по которому запрашивает кеш org.ayfaar.app.controllers.search.cache.DbCache.get()
    private Integer key;
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
