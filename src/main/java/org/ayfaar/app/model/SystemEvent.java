package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ayfaar.app.services.moderation.Action;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

import static org.ayfaar.app.utils.hibernate.EnumHibernateType.CLASS;
import static org.ayfaar.app.utils.hibernate.EnumHibernateType.ENUM;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SystemEvent {
    @Id
    @GeneratedValue
    private Integer id;
    private String message;
    private String user;
    @Column(columnDefinition = "text")
    private String command;
    private Date createdAt = new Date();
    @Type(type = ENUM, parameters = @org.hibernate.annotations.Parameter(name = CLASS, value = "org.ayfaar.app.services.moderation.Action"))
    private Action action;
    private String confirmedByUser;
}
