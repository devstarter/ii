package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;
import org.ayfaar.app.utils.Language;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import static org.ayfaar.app.utils.EnumHibernateType.CLASS;
import static org.ayfaar.app.utils.EnumHibernateType.ENUM;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "видео:youtube:", field = "id")
@Getter @Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class VideoResource extends UID {
    @Column(unique = true, nullable = false)
    @NonNull
    private String id;

    @NonNull
    @Column(nullable = false)
    @Type(type = ENUM, parameters = @Parameter(name = CLASS, value = "org.ayfaar.app.utils.Language"))
    private Language lang;
}
