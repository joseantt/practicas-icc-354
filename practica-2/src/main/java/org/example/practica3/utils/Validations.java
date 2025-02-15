package org.example.practica3.utils;

import org.example.practica3.entities.Project;
import org.springframework.security.core.context.SecurityContextHolder;

public class Validations {
    public static boolean userCanEnter(Project project) {
        var authContext = SecurityContextHolder.getContext().getAuthentication();

        String role = authContext.getAuthorities().stream().findFirst().get().getAuthority();
        String username = authContext.getName();

        return role.equals("ROLE_ADMIN") || project.getUserInfo().getUsername().equals(username);
    }
}
