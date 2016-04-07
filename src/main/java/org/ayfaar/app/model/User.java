package org.ayfaar.app.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Column()
    private Long id;
    @Column()
    private String firstname;
    @Column()
    private String lastname;
    @Id
    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private String role;

    public User(String email) {
        this.email = email;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}