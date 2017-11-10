package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="uri")
//@Audited
@Uri(nameSpace = "статья:", field = "id")
@Getter
@Setter
@NoArgsConstructor
public class Article extends UID {

    public static final Class SEQUENCE = ArticleSeq.class;

    @Column(unique = true)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @Column(columnDefinition = "TEXT")
    private String taggedContent;

    public Article(String name, String content) {
        this.name = name;
        this.content = content;
    }


    @Override
    public String toTitle() {
        return name;
    }
}
