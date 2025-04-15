package org.example.practica8.repositories;

import org.example.practica8.entities.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    UserInfo findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    void deleteByEmailIgnoreCase(String email);

    /**
     * Find users by role
     * @param role Role to filter by
     * @param pageable Pagination information
     * @return Page of users with the specified role
     */
    Page<UserInfo> findByRole(String role, Pageable pageable);

    /**
     * Find users by role and name or email containing the search term (case insensitive)
     * @param role1 Role for name search
     * @param nameTerm Name search term
     * @param role2 Role for email search (typically same as role1)
     * @param emailTerm Email search term
     * @param pageable Pagination information
     * @return Page of users matching the search criteria
     */
    Page<UserInfo> findByRoleAndNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
            String role1, String nameTerm, String role2, String emailTerm, Pageable pageable);

    /**
     * Count users by role
     * @param role Role to count
     * @return Number of users with the specified role
     */
    long countByRole(String role);
}