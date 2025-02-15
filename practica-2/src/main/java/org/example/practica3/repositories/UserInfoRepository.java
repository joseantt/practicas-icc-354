package org.example.practica3.repositories;

import org.example.practica3.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByUsername(String username);
    boolean existsByUsernameIgnoreCase(String username);
    void deleteByUsername(String username);
}
