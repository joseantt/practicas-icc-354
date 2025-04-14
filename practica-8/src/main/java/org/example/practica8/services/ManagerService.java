package org.example.practica8.services;

import org.example.practica8.constants.Role;
import org.example.practica8.entities.Manager;
import org.example.practica8.repositories.ManagerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final BCryptPasswordEncoder encoder;

    public ManagerService(ManagerRepository managerRepository, BCryptPasswordEncoder encoder) {
        this.managerRepository = managerRepository;
        this.encoder = encoder;
    }

    public Page<Manager> findAll(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return managerRepository.findAll(pageable);
        } else {
            return managerRepository.findBySearchTerm(searchTerm, pageable);
        }
    }

    public long count() {
        return managerRepository.count();
    }

    public Optional<Manager> findById(Long id) {
        return managerRepository.findById(id);
    }

    @Transactional
    public void save(Manager manager) {
        if (manager.getId() == null) {
            // Nuevo gerente
            manager.setRole(Role.MANAGER);
            if (manager.getPassword() != null) {
                manager.setPassword(encoder.encode(manager.getPassword()));
            }
        } else {
            // Gerente existente, verificar si la contraseña ha cambiado
            managerRepository.findById(manager.getId()).ifPresent(existing -> {
                if (!manager.getPassword().equals(existing.getPassword())) {
                    manager.setPassword(encoder.encode(manager.getPassword()));
                }
            });
        }
        managerRepository.save(manager);
    }

    @Transactional
    public void delete(Manager manager) {
        managerRepository.delete(manager);
    }

    @Transactional
    public void updateProfile(Manager manager, String name, String email) {
        manager.setName(name);
        manager.setEmail(email);
        managerRepository.save(manager);
    }

    @Transactional
    public void updatePassword(Manager manager, String newPassword) {
        manager.setPassword(encoder.encode(newPassword));
        managerRepository.save(manager);
    }
}