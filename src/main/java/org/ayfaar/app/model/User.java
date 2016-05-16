package org.ayfaar.app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ayfaar.app.controllers.OAuthProvider;
import org.ayfaar.app.services.moderation.UserRole;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String accessToken;
    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String name;
    private String picture;
    private String timezone;
    private String thumbnail;
    @Enumerated(EnumType.STRING)
    private OAuthProvider oauthProvider;
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_AUTHENTICATED;
    private Date createdAt = new Date();
    private Date lastVisitAt;
    private Long providerId;

    @Builder
    public User(String email, String accessToken, String firstName, String lastName, String name, String picture, String thumbnail, String timezone, Long providerId, OAuthProvider oauthProvider) {
        this.email = email;
        this.accessToken = accessToken;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.picture = picture;
        this.thumbnail = thumbnail;
        this.timezone = timezone;
        this.providerId = providerId;
        this.oauthProvider = oauthProvider;
    }
}