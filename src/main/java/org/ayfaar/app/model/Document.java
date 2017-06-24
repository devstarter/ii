package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "документ:google:", field = "id")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Document extends UID /*implements HasSequence<DocumentSeq>*/ {
    @NonNull
    @Column(unique = true)
    private String id;
    @Column(nullable = false)
    @NonNull
    private String name;
    @Column(columnDefinition = "text")
    private String annotation;
    private String author;
    private String thumbnail;
    private String mimeType;
    private String icon;
    private String downloadUrl;
    private Date createdAt = new Date();

    @Builder
    public Document(String id, String name, String annotation, String author, String thumbnail, String mimeType, String icon, String downloadUrl) {
        this.id = id;
        this.name = name;
        this.annotation = annotation;
        this.author = author;
        this.thumbnail = thumbnail;
        this.mimeType = mimeType;
        this.icon = icon;
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toTitle() {
        return name;
    }

    /*@Override
    public Class<DocumentSeq> getSequence() {
        return DocumentSeq.class;
    }*/


}
