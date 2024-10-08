package com.booleanuk.api.security;

import com.booleanuk.api.model.ERole;
import com.booleanuk.api.model.Role;
import com.booleanuk.api.security.jwt.AuthEntryPointJwt;
import com.booleanuk.api.security.jwt.AuthTokenFilter;
import com.booleanuk.api.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf.disable())
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/auth/**").permitAll() // all incoming requests
                        .requestMatchers(HttpMethod.GET, "/items", "/items/**").authenticated() // all users (admin + normal user)
                        .requestMatchers(HttpMethod.POST, "/items", "/items/**").hasAuthority(ERole.ADMIN.toString()) // only admin
                        .requestMatchers(HttpMethod.PUT, "/items", "/items/**").hasAuthority(ERole.ADMIN.toString()) // only admin
                        .requestMatchers(HttpMethod.DELETE, "/items", "/items/**").hasAuthority(ERole.ADMIN.toString()) // only admin

                        .requestMatchers(HttpMethod.GET, "/lend/user/{userId}").hasAuthority(ERole.USER.toString())
                        .requestMatchers(HttpMethod.POST, "/lend").hasAuthority(ERole.USER.toString())
                        .requestMatchers(HttpMethod.POST, "/lend/return").hasAuthority(ERole.USER.toString())

                        .requestMatchers(HttpMethod.GET, ("/lend/{userId}/*")).hasAuthority(ERole.ADMIN.toString())
                        .requestMatchers(HttpMethod.GET, ("/lend/item/{itemId}")).hasAuthority(ERole.ADMIN.toString())
                );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}