package org.example.practica8.services;

import org.example.practica8.entities.UserInfo;
import org.example.practica8.repositories.UserInfoRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final BCryptPasswordEncoder encoder;

    public UserInfoService(UserInfoRepository userInfoRepository, BCryptPasswordEncoder encoder) {
        this.userInfoRepository = userInfoRepository;
        this.encoder = encoder;
    }

    public void save(UserInfo userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userInfoRepository.save(userInfo);
    }

    public UserInfo findByEmail(String email) {
        return userInfoRepository.findByEmailIgnoreCase(email);
    }

    public boolean existsByEmail(String email) {
        return userInfoRepository.existsByEmailIgnoreCase(email);
    }

    public void deleteByEmail(String email) {
        userInfoRepository.deleteByEmailIgnoreCase(email);
    }

    public void deleteById(Long id) {
        userInfoRepository.deleteById(id);
    }

    public List<UserInfo> findAll() {
        return userInfoRepository.findAll();
    }

    public long count() {
        return userInfoRepository.count();
    }
}
