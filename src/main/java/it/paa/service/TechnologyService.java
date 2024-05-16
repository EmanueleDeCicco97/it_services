package it.paa.service;

import it.paa.model.Client;
import it.paa.model.Employee;
import it.paa.model.Project;
import jakarta.enterprise.context.ApplicationScoped;
import it.paa.model.Technology;
import it.paa.repository.TechnologyRepository;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class TechnologyService implements TechnologyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    ClientService clientService;


    @Override  //metodo per recuperare le tecnologie in base all'id
    public Technology findById(Long id) throws NotFoundException {
        Technology technology = entityManager.find(Technology.class, id);
        if (technology == null) {
            throw new NotFoundException("Technology not found with id: " + id);
        }
        return technology;
    }

    @Transactional
    @Override //metodo per salvare una tecnologia
    public Technology save(Technology technology) {

        entityManager.persist(technology);

        return technology;
    }

    @Transactional
    @Override //metodo per aggiornare una tecnologia
    public Technology update(Technology technology) throws NotFoundException, IllegalArgumentException {
        entityManager.merge(technology);
        return technology;
    }

    @Transactional
    @Override //metodo per cancellare una tecnologia
    public void delete(Long id) throws NotFoundException {
        Technology technologyToDelete = findById(id);
        entityManager.remove(technologyToDelete);
    }

    @Override //metodo per trovare tutte le tecnologie in base ai criteri forniti
    public List<Technology> findAllByAttributes(String name, String experienceLevel) {
        List<Technology> technologies = entityManager.createQuery(" from Technology ", Technology.class)
                .getResultList();
        if (name != null && !name.isEmpty() && !name.isBlank() && experienceLevel != null && !experienceLevel.isEmpty() && !experienceLevel.isBlank()) {
            technologies = technologies.stream()
                    .filter(tech -> tech.getName().equalsIgnoreCase(name))
                    .filter(tech -> tech.getRequiredExperienceLevel().equalsIgnoreCase(experienceLevel))
                    .collect(Collectors.toList());
        } else if (name != null && !name.isEmpty() && !name.isBlank()) {
            technologies = technologies.stream()
                    .filter(tech -> tech.getName().equalsIgnoreCase(name))
                    .collect(Collectors.toList());
        } else if (experienceLevel != null && !experienceLevel.isEmpty() && !experienceLevel.isBlank()) {
            technologies = technologies.stream()
                    .filter(tech -> tech.getRequiredExperienceLevel().equalsIgnoreCase(experienceLevel))
                    .collect(Collectors.toList());
        }

        return technologies;
    }

    //•	Esercitazione 2: Creare un endpoint per trovare le tecnologie più richieste dai clienti e visualizzare i dettagli
    // dei progetti in cui sono utilizzate queste tecnologie.
    @Override
    public Map<String, Set<Project>> findMostTechnology() {
        //creo le liste e i set
        List<Technology> technologies = new ArrayList<Technology>();
        List<Client> clients = clientService.findAll();
        //utilizzo un set per evitare duplicati
        Set<Employee> employees = new HashSet<>();

        //lista employee associati ai clienti
        clients.forEach(client -> {
            employees.add(client.getContactPerson());
        });

        //filtriamo gli employee che hanno progetti associati
        List<Employee> employeesFiltered = employees.stream().filter(employee -> !employee.getProjects().isEmpty()).toList();

        // vado a prendere tutte le tecnologie associate agli employee filtrati
        employeesFiltered.forEach(employee -> {
            technologies.addAll(employee.getTechnologies());
        }); //lista tecnologie associate agli employee

        // trovo la tecnologia più ricorrente
        Optional<Technology> mostCommonTechnology = technologies.stream()
                //raggruppo creando una map con la tecnologia come chiave e il numero di occorrenze
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                // trovo la tecnologia più ricorrente
                .max(Map.Entry.comparingByValue())
                // restituisco la tecnologia
                .map(Map.Entry::getKey);

        // filtriamo gli employee che hanno la tecnologia più ricorrente
        List<Employee> esmployeesPopularTechnology = employeesFiltered.stream()
                .filter(employee -> employee.getTechnologies()
                        .contains(mostCommonTechnology.orElse(null))).toList();
        // filtriamo i progetti associati agli employee che hanno la tecnologia più ricorrente

        //utilizzo un set per evitare duplicati
        Set<Project> projectsPopularTechnology = new HashSet<>();

        esmployeesPopularTechnology.forEach(employee -> {
            projectsPopularTechnology.addAll(employee.getProjects());

        });
        //restituisco la mappa con la tecnologia più ricorrente e i progetti associati
        //utilizzo un map per far uscire il nome della tecnologia e tutti i progetti associati
        Map<String, Set<Project>> technologyProjectMap = new HashMap<>();
        technologyProjectMap.put(mostCommonTechnology.orElse(null).getName(), projectsPopularTechnology);

        return technologyProjectMap;
    }

    //Validazioni avanzate (facoltative)  (continuo validazione)
    //Dipendente: Assicurarsi che il ruolo sia congruo rispetto all'esperienza del dipendente.
    @Override // Confronto tra role employee e experience technology
    public boolean isEmployeeExperienceValid(String experienceLevel, String role) {
        switch (experienceLevel.toLowerCase()) {
            case "senior":

                return role.equalsIgnoreCase("senior");
            case "junior":

                return true;
            case "middle":

                return role.equalsIgnoreCase("middle") || role.equalsIgnoreCase("senior");

            case "project manager":

                return role.equalsIgnoreCase("middle") || role.equalsIgnoreCase("senior") || role.equalsIgnoreCase("project manager");

            default:

                return false;
        }
    }

    public Technology getTechnologyByNameIgnoreCase(String name) {
        TypedQuery<Technology> query = entityManager.createQuery("SELECT t FROM Technology t WHERE LOWER(t.name) = LOWER(:name)", Technology.class);
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
