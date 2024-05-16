package it.paa.repository;

import it.paa.dto.ProjectDto;
import it.paa.model.Project;
import it.paa.model.Technology;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProjectRepository {

    Project findById(Long id);

    Project save(Project project);

    Project update(Long id, ProjectDto projectDto);

    void delete(Long id);

    List<Project> findAllByAttributes(String name, LocalDate startDate);

    Map<Project, Set<Technology>> getProjectsWithTechnologies();

}


