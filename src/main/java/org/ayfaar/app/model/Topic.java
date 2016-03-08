package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;
import org.ayfaar.app.utils.Language;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

import static org.ayfaar.app.utils.Language.ru;
import static org.ayfaar.app.utils.hibernate.EnumHibernateType.CLASS;
import static org.ayfaar.app.utils.hibernate.EnumHibernateType.ENUM;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "тема:")
@Getter @Setter
@NoArgsConstructor
@RequiredArgsConstructor
/**
 * Сущность хранящая название некой произвольной темы
 */
public class Topic extends UID {
    @Column(unique = true)
    @NonNull
    private String name;

    @Column(nullable = false)
    @Type(type = ENUM, parameters = @Parameter(name = CLASS, value = "org.ayfaar.app.utils.Language"))
    private Language lang = ru;

    private Date createdAt = new Date();
}
