package org.example.practica8.services;

import org.example.practica8.constants.Role;
import org.example.practica8.entities.UserDetailsImpl;
import org.example.practica8.entities.UserInfo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserInfoService userInfoService;

    public UserDetailsServiceImpl(UserInfoService userInfoService, BCryptPasswordEncoder encoder) {
        this.userInfoService = userInfoService;

        if(userInfoService.count() == 0) {
            UserInfo admin = UserInfo.builder()
                            .email("admin")
                            .password("admin")
                            .role(Role.ADMIN).build();
            userInfoService.save(admin);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo user = userInfoService.findByEmail(email);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UserDetailsImpl(user);
    }
}
