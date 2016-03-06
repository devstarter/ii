package org.ayfaar.app.configs;

import org.ayfaar.app.spring.authentication.provider.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;


@Configuration
@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("currentUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/static/old/adm.html")
                .hasRole("USER")
                //.anyRequest().authenticated()

                .and()
                .httpBasic()
                //.and().formLogin().defaultSuccessUrl("/static/old/adm.html", false).failureUrl("/")
                //.anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                        .and()
                        .csrf()//Disabled CSRF protection
                        .disable();

                //.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
                //.anyRequest().fullyAuthenticated();
////                .and()
////                .formLogin()
////                .loginPage("/login")
////                .permitAll()
////                .and()
////                .logout()
////                .permitAll();

//        http
//                .authorizeRequests().anyRequest().authenticated()
//                .and()
//                .httpBasic();

    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(authProvider);
//      auth.userDetailsService(userDetailsService);
//                //.passwordEncoder(new BCryptPasswordEncoder());
//

    }
}