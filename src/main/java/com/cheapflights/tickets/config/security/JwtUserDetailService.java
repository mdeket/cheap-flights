package com.cheapflights.tickets.config.security;

import com.cheapflights.tickets.domain.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: Find user and return in or else throw Exception
        return new User();
//        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}