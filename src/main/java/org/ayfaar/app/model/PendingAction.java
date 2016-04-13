package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ayfaar.app.services.moderation.Action;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PendingAction {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private String initiatedBy;
    @Column(columnDefinition = "text", nullable = false)
    private String command;
    private Date createdAt = new Date();
    @Enumerated(EnumType.STRING)
    private Action action;
    private Date confirmedAt;
    private String confirmedBy;
}
