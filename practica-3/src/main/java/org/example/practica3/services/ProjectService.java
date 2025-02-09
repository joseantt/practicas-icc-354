package org.example.practica3.services;

import org.example.practica3.entities.Project;
import org.example.practica3.repositories.ProjectRepository;

public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project fingByUserId(long id) {
        return null;
    }
}
