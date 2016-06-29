package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ayfaar.app.services.moderation.Action;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
public class ActionEvent {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Date createdAt = new Date();
    @Column(nullable = false, columnDefinition = "text")
    private String message;
    private Integer createdBy;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;
}
