package org.ayfaar.app.model;

import lombok.Getter;
import lombok.Setter;
import org.ayfaar.app.services.moderation.AccessLevel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
@Getter @Setter
public class CustomUser implements UserDetails {

    private String username;
    private String password;//необходим из-за implements UserDetails
    private String email;
    private String firstname;
    private String lastname;

    /* Spring Security related fields*/
    private List<GrantedAuthority> authorities;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
}
