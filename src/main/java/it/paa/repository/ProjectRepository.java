package it.paa.repository;

import it.paa.dto.ProjectDto;
import it.paa.model.Project;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository {

    Project findById(Long id);

    Project save(Project project);

    Project update(Long id, ProjectDto projectDto);

    void delete(Long id);

    List<Project> findAllByAttributes(String name, LocalDate startDate);

}


