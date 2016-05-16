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
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false)
    private Date createdAt = new Date();
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private Integer userId;
    @Column(nullable = false)
    private Action action;
}
