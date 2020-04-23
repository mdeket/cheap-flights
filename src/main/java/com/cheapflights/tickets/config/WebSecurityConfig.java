package com.cheapflights.tickets.config;

import com.cheapflights.tickets.config.security.JwtAuthenticationEntryPoint;
import com.cheapflights.tickets.config.security.JwtConfigurer;
import com.cheapflights.tickets.config.security.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final TokenProvider tokenProvider;

    public WebSecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, TokenProvider tokenProvider) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.tokenProvider = tokenProvider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/user/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .and()
                .httpBasic()
                .and()
                .apply(securityConfigurerAdapter());

        // Adds a filter to validate the tokens with every request
    }

    private JwtConfigurer securityConfigurerAdapter() {
        return new JwtConfigurer(tokenProvider);
    }
}