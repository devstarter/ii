package org.ayfaar.app.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private String role;
    @Column()
    private String firstname;
    @Column()
    private String lastname;
    @Column()
    private String name;
    @Column()
    private String thumbnail;
    @Column()
    private String authProvider;

    public User(String email) {
        this.email = email;
    }

    public User() {
    }
}