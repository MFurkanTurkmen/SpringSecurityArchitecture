package com.mft.springsecurity.config;

import com.mft.springsecurity.entity.MyUser;
import com.mft.springsecurity.entity.Role;
import com.mft.springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JwtUserDetails implements UserDetailsService {

    @Autowired
    UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> myUser = userRepository.findOptionalByUsername(username);
        if (myUser.isEmpty()) throw new UsernameNotFoundException("User not found with username: " + username);

        List<GrantedAuthority> authorities=new ArrayList<>();

        for (Role role : myUser.get().getRoles()) {
            new SimpleGrantedAuthority(role.getName().name());
        }
        // add
        return new User(myUser.get().getUsername(), myUser.get().getPassword(), authorities);

    }


}
