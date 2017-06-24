package org.ayfaar.app.model;

import lombok.*;
import org.ayfaar.app.utils.Language;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Translation {
    @Id
    @NonNull
    private String origin;
    @Column(nullable = false)
    @NonNull
    private String translated;
    @NonNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language lang;
    @Enumerated(EnumType.STRING)
    private Context context = Context.topic;

    public enum Context {
        topic
    }
}
