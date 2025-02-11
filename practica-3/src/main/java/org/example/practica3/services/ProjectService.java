package org.example.practica3.services;

import org.example.practica3.entities.Project;
import org.example.practica3.repositories.ProjectRepository;

<<<<<<< Updated upstream
=======
import java.util.List;
import java.util.Optional;

@Service
>>>>>>> Stashed changes
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

<<<<<<< Updated upstream
    public Project fingByUserId(long id) {
        return null;
=======
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
>>>>>>> Stashed changes
    }
}
