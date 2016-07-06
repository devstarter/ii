package org.ayfaar.app.configs;

import org.ayfaar.app.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/static/old/adm.html")
                .access("hasRole('ADMIN')")
                .and().logout().logoutSuccessUrl("/")
                .and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider());
    }

    @Component
    public static class CustomAuthenticationProvider implements AuthenticationProvider {
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            User user = (User)authentication.getPrincipal();
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.getRole().toString());
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return true;
        }
    }
}
