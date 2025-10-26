package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.exceptions.ResourceNotFoundException;
import com.example.demo4.SecurityApp.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private UserRepositories userRepositories;
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepositories
//                .findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User with email = " + email + " not found..."));
//        return user;
//    }
//}
