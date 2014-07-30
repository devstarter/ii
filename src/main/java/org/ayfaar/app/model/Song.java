package org.ayfaar.app.model;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Song {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String info;
    @Column(columnDefinition = "TEXT")
    private String content;

    public Song(Integer id, String name, String info, String content) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.content = content;
    }

}
