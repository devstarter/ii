package org.ayfaar.app.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ayfaar.app.services.moderation.AccessLevel;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private String email;
    @Column()
    private Long id;
    @Column()
    private String role1;
    @Column()
    private String first_name;
    @Column()
    private String last_name;
    @Column()
    private String name;
    @Column()
    private String thumbnail;
    @Column()
    private String auth_provider;
    @Column()
    @Enumerated(EnumType.STRING)
    private AccessLevel role;

    public User(String email) {
        this.email = email;
    }

    public User() {
    }
}