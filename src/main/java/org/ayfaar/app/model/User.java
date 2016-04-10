package org.ayfaar.app.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ayfaar.app.services.moderation.AccessLevel;

@Entity
@Getter @Setter
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private String email;
    private Long id;
    private String first_name;
    private String last_name;
    private String name;
    private String thumbnail;
    private String auth_provider;
    @Enumerated(EnumType.STRING)
    private AccessLevel role;

    public User(String email) {
        this.email = email;
    }

    public User() {
    }
}