package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "изображение:", field = "id")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Image extends UID {

    @NonNull
    @Column(unique = true)
    private String id;
    @Column(nullable = false)
    @NonNull
    private String name;
    private String mimeType;
    private String downloadUrl;
    private String thumbnail;
    private Date createdAt = new Date();
    @Column(columnDefinition = "text")
    private String comment;

    @Builder
    public Image(String id, String name, String mimeType, String downloadUrl, String thumbnail) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.downloadUrl = downloadUrl;
        this.thumbnail = thumbnail;
    }

    @Override
    public String toTitle() {
        return name;
    }
}
