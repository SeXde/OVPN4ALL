package com.aberdote.OVPN4ALL.security.service;

import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service @Transactional
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByNameIgnoreCase(username)
                .map(userEntity ->
                        new User(userEntity.getName(),
                                userEntity.getPassword(),
                                userEntity.getRoles()
                                        .stream()
                                        .map(role ->
                                                new SimpleGrantedAuthority(role.getRoleName()))
                                        .toList()))
                .orElseThrow(() -> new CustomException("User " +username+" not found", HttpStatus.NOT_FOUND));
    }
}