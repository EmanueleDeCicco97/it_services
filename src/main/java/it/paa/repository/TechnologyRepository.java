package it.paa.repository;

import it.paa.model.Project;
import it.paa.model.Technology;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TechnologyRepository {

    Technology findById(Long id);

    Technology save(Technology technology);

    Technology update(Technology technology);

    void delete(Long id);

    List<Technology> findAllByAttributes(String name, String experienceLevel);

    boolean isEmployeeExperienceValid(String experienceLevel, String role);

    Map<Technology, Set<Project>> findMostTechnologies();

    Technology getTechnologyByNameIgnoreCase(String name);
}
