package org.ayfaar.ii.model;
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

    public Song() {
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
