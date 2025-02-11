package org.example.practica3.services;

import org.example.practica3.entities.Project;
import org.example.practica3.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> findByProjectId(long projectId) {
        return projectRepository.findById(projectId);
    }

    public List<Project> findByUserId(long userId) {
        return projectRepository.findByUserInfo_Id(userId);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    public void saveProject(Project project) {
        projectRepository.save(project);
    }
}
