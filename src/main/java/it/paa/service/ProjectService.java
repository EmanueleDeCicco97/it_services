package it.paa.service;

import it.paa.dto.ProjectDto;
import it.paa.model.Employee;
import it.paa.model.Project;
import it.paa.model.Technology;
import it.paa.repository.ProjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProjectService implements ProjectRepository {

    @PersistenceContext
    private EntityManager entityManager;


    // ricerca del progetto in base all'id
    @Override
    public Project findById(Long id) throws NotFoundException {
        Project project = entityManager.find(Project.class, id);
        if (project == null) {
            throw new NotFoundException("Project not found with id: " + id);
        }
        return project;
    }

    // metodo per salvare il project
    @Transactional
    @Override
    public Project save(Project project) {
        entityManager.persist(project);
        return project;
    }

    // metodo per aggiornare un project
    @Transactional
    @Override
    public Project update(Long id, ProjectDto projectDto) throws NotFoundException {
        // recupero i dati e li inserisco in una oggetto
        Project existingProject = entityManager.find(Project.class, id);

        // se l'oggetto è null lancio l'eccezzione
        if (existingProject == null) {
            throw new NotFoundException("Project not found with id: " + id);
        }
        // Aggiorno le informazioni del progetto esistente con quelle fornite nel projectDto
        existingProject.setName(projectDto.getName());
        existingProject.setDescription(projectDto.getDescription());
        existingProject.setStartDate(projectDto.getStartDate());
        existingProject.setEndDate(projectDto.getEndDate());

        // Salva il progetto aggiornato nel repository
        entityManager.merge(existingProject);

        return existingProject;
    }

    // metodo per eliminare un project
    @Transactional
    @Override
    public void delete(Long id) throws NotFoundException {
        Project projectToDelete = findById(id);
        entityManager.remove(projectToDelete);
    }

    // cerco tutti i project in base ai 2 attrubuti inseriti
    @Override
    public List<Project> findAllByAttributes(String name, LocalDate startDate) {

        // recupero i dati e li inserisco in una lista
        List<Project> filteredProjects = entityManager.createQuery("from Project", Project.class).getResultList();

        // filtro per la lista
        return filteredProjects.stream()
                .filter(project -> project.getName().equalsIgnoreCase(name))
                .filter(project -> startDate == null || project.getStartDate().equals(startDate))
                .collect(Collectors.toList());
    }


    //•	API pubblica di visualizzazione dei progetti con dettaglio delle tecnologie
    public Map<Project, Set<Technology>> getProjectsWithTechnologies() {

        Map<Project, Set<Technology>> map = new HashMap<>();
        List<Project> projects = new ArrayList<>();

        projects = entityManager.createQuery("from Project", Project.class).getResultList();
        projects.forEach(project -> {
            Set<Technology> technologies = new HashSet<>();

            project.getEmployees().forEach(employee ->
            {
                technologies.addAll(employee.getTechnologies());
            });
            map.put(project, technologies);
        });

        return map;
    }
}
