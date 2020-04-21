package com.cheapflights.tickets.config.security;

import com.cheapflights.tickets.domain.model.User;
import com.cheapflights.tickets.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;

    public CustomAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByUsername((String) authentication.getPrincipal());
        return optionalUser.map(user -> {
            String plainPassword = (String) authentication.getCredentials();
            if (!BCrypt.checkpw(plainPassword.trim(), user.getPassword())) {
                throw new BadCredentialsException("Username or password is wrong.");
            }
            return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), Arrays.asList(new SimpleGrantedAuthority(user.getRole().toString())));
        })
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s was not found in the database", authentication.getPrincipal())));
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.equals(aClass);
    }
}
