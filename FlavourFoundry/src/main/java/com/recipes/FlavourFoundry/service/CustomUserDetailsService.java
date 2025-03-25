package com.recipes.FlavourFoundry.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.recipes.FlavourFoundry.model.User;
import com.recipes.FlavourFoundry.model.UserPrincipal;
import com.recipes.FlavourFoundry.repository.UserRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Email: " + email);
        Optional<User> user = userRepo.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("user not found");
        }

        return new UserPrincipal(user.get());
    }

}
