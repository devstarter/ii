package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "документ:", field = "id")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Document extends UID implements HasSequence<DocumentSeq> {
    @Column(unique = true)
    private Integer id;
    @Column(nullable = false)
    @NonNull
    private String name;
    private String pdfUrl;
    @Column(columnDefinition = "text")
    private String annotation;
    private String author;
    private Date createdAt = new Date();

    @Builder
    public Document(String name, String pdfUrl, String annotation, String author) {
        this.name = name;
        this.pdfUrl = pdfUrl;
        this.annotation = annotation;
        this.author = author;
    }

    @Override
    public String toTitle() {
        return name;
    }

    @Override
    public Class<DocumentSeq> getSequence() {
        return DocumentSeq.class;
    }


}
