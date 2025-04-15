package org.example.practica8.services;

import org.example.practica8.constants.Role;
import org.example.practica8.entities.UserInfo;
import org.example.practica8.repositories.UserInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;
    private final BCryptPasswordEncoder encoder;

    public UserInfoService(UserInfoRepository userInfoRepository, BCryptPasswordEncoder encoder) {
        this.userInfoRepository = userInfoRepository;
        this.encoder = encoder;
    }

    @Transactional
    public void save(UserInfo userInfo) {
        if(userInfo.getPassword() != null && !passwordImplementsBCrypt(userInfo.getPassword())) {
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        }

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

    public Page<UserInfo> findAll(Pageable pageable) {
        return userInfoRepository.findAll(pageable);
    }

    /**
     * Find all users with the manager role, with optional filtering by name or email
     * @param searchTerm The search term to filter by (name or email)
     * @param pageable Pagination information
     * @return Page of managers matching the criteria
     */
    public Page<UserInfo> findAllManagers(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return userInfoRepository.findByRole(Role.MANAGER, pageable);
        } else {
            String likePattern = "%" + searchTerm.toLowerCase() + "%";
            return userInfoRepository.findByRoleAndNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
                    Role.MANAGER, likePattern, Role.MANAGER, likePattern, pageable);
        }
    }

    /**
     * Find a user by ID
     * @param id The user ID
     * @return Optional containing the user if found
     */
    public Optional<UserInfo> findById(Long id) {
        return userInfoRepository.findById(id);
    }

    /**
     * Delete a user
     * @param user The user to delete
     */
    @Transactional
    public void delete(UserInfo user) {
        userInfoRepository.delete(user);
    }

    public long count() {
        return userInfoRepository.count();
    }

    /**
     * Count managers
     * @return Number of users with manager role
     */
    public long countManagers() {
        return userInfoRepository.countByRole(Role.MANAGER);
    }

    private boolean passwordImplementsBCrypt(String password) {
        return password != null &&
                (password.startsWith("$2a$") ||
                        password.startsWith("$2b$") ||
                        password.startsWith("$2y$"));
    }
}