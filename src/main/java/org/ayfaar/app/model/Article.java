package org.ayfaar.app.model;

import org.ayfaar.app.annotations.Uri;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Audited
@Uri(nameSpace = "статья:")
public class Article extends UID {

    public static final Class SEQUENCE = ArticleSeq.class;

    @Column(unique = true)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public Article(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public Article() {
    }

    public Integer getId() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
