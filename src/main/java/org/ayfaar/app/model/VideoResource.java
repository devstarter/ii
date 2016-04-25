package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;
import org.ayfaar.app.utils.AdvanceComparator;
import org.ayfaar.app.utils.Language;

import javax.persistence.*;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "видео:youtube:", field = "id")
@Getter @Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class VideoResource extends UID implements Comparable<VideoResource> {
    @Column(unique = true, nullable = false)
    @NonNull
    private String id;
    private String title;

    @NonNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language lang;

    private Date publishedAt = new Date();
    private Date createdAt = new Date();
    private Integer createdBy;

    @Override
    public String toTitle() {
        return title;
    }

    @Override
    public int compareTo(VideoResource o) {
        return AdvanceComparator.INSTANCE.compare(title, o.title);
    }

}
