package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "url:", field = "uri")
@Getter @Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Resource extends UID {
    @Column(nullable = false)
    @NonNull
    private String title;

    @NonNull
    @Column(nullable = false)
    private ResourceType type;

    @Column(columnDefinition = "TEXT")
    private String comments;

    private Date createdAt = new Date();

    @Override
    public String toTitle() {
        return title;
    }

    public String getUrl() {
        return uri.replaceFirst("url:", "");
    }
}
