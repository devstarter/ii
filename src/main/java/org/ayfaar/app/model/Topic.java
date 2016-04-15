package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.annotations.Uri;
import org.ayfaar.app.utils.Language;

import javax.persistence.*;
import java.util.Date;

import static org.ayfaar.app.utils.Language.ru;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "тема:")
@Getter
@Setter
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
    @Enumerated(EnumType.STRING)
    private Language lang = ru;
    private Date createdAt = new Date();

    @Override
    public String toTitle() {
        return name;
    }
}
