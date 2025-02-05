package org.example.practica3.services;

import org.example.practica3.entities.UserDetailsImpl;
import org.example.practica3.enums.Role;
import org.example.practica3.repositories.UserInfoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.practica3.entities.UserInfo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserInfoRepository userInfoRepository;

    public UserDetailsServiceImpl(UserInfoRepository userInfoRepository, BCryptPasswordEncoder encoder) {
        this.userInfoRepository = userInfoRepository;

        if(userInfoRepository.count() == 0) {
            UserInfo admin = UserInfo.builder()
                            .username("admin")
                            .password(encoder.encode("admin"))
                            .role(Role.ADMIN).build();
            userInfoRepository.save(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userInfoRepository.findByUsername(username);
        if(user == null) {
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not found");
        }

        return new UserDetailsImpl(user);
    }
}
