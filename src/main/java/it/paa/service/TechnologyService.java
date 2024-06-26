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
    public Technology update(Technology technology) throws NotFoundException {
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
//    @Override
//    public Map<String, Set<Project>> findMostTechnology() {
//        // trovo tutti i clienti e ottengo i loro employee
//        List<Client> clients = clientService.findAll();
//        Set<Employee> employees = clients.stream()
//                .map(Client::getContactPerson)
//                .collect(Collectors.toSet());
//
//        // filtro gli employee che hanno progetti associati
//        List<Employee> employeesWithProjects = employees.stream()
//                .filter(employee -> !employee.getProjects().isEmpty())
//                .toList();
//
//        // ottengo tutte le tecnologie associate agli employee filtrati
//        List<Technology> technologies = employeesWithProjects.stream()
//                .flatMap(employee -> employee.getTechnologies().stream())
//                .toList();
//
//        // trovo la tecnologia più ricorrente
//        Optional<Technology> mostCommonTechnology = technologies.stream()
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
//                .entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey);
//
//        // Se non ci sono tecnologie, ritorno una mappa vuota
//        if (mostCommonTechnology.isEmpty()) {
//            return Collections.emptyMap();
//        }
//
//        // filtro gli employee che utilizzano la tecnologia più ricorrente
//        Set<Project> projectsUsingMostCommonTechnology = employeesWithProjects.stream()
//                .filter(employee -> employee.getTechnologies().contains(mostCommonTechnology.get()))
//                .flatMap(employee -> employee.getProjects().stream())
//                .collect(Collectors.toSet());
//
//        // creo la mappa con il nome della tecnologia e i progetti associati
//        Map<String, Set<Project>> technologyProjectMap = new HashMap<>();
//        technologyProjectMap.put(mostCommonTechnology.get().getName(), projectsUsingMostCommonTechnology);
//
//        return technologyProjectMap;
//    }

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

    //query di partenza
    //select c.* from client c
    //inner join employee_project ep on c.contact_person_id = ep.employee_id
    //inner join employee_technology et on c.contact_person_id = et.employee_id

    // 2 query per avvicinarmi alle tecnologie più utilizzate
    //SELECT t.*, COUNT(*) AS usage_count
    //FROM technology t
    //INNER JOIN (
    //    SELECT et.technology_id
    //    FROM client c
    //    INNER JOIN employee_project ep ON c.contact_person_id = ep.employee_id
    //    INNER JOIN employee_technology et ON ep.employee_id = et.employee_id
    //) AS tech ON t.id = tech.technology_id
    //GROUP BY t.id
    //HAVING COUNT(*) >= 2
    //ORDER BY usage_count DESC;

    //•	Esercitazione 2: Creare un endpoint per trovare le tecnologie più richieste dai clienti e visualizzare i dettagli
    // dei progetti in cui sono utilizzate queste tecnologie.
    public Map<Technology, Set<Project>> findMostTechnologies() {
        // recupero tutti i clienti e i loro contatti (employee)
        List<Client> clients = clientService.findAll();
        Set<Employee> employees = clients.stream()
                .map(Client::getContactPerson)
                .collect(Collectors.toSet());

        // filtro gli employee che hanno progetti associati
        List<Employee> employeesWithProjects = employees.stream()
                .filter(employee -> !employee.getProjects().isEmpty())
                .toList();

        // ottengo tutte le tecnologie associate agli employee filtrati
        List<Technology> allTechnologies = employeesWithProjects.stream()
                .flatMap(employee -> employee.getTechnologies().stream())
                .toList();

        // conto le occorrenze di ciascuna tecnologia
        Map<Technology, Long> technologyOccurrences = allTechnologies.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // trovo il numero massimo di occorrenze tra tutte le tecnologie
        long maxOccurrences = Collections.max(technologyOccurrences.values());

        // ottengo tutte le tecnologie che hanno lo stesso numero massimo di occorrenze
        Set<Technology> mostUsedTechnologies = technologyOccurrences.entrySet().stream()
                .filter(entry -> entry.getValue() == maxOccurrences)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // mappo per memorizzare i progetti associati a ciascuna tecnologia più utilizzata
        Map<Technology, Set<Project>> technologyProjectMap = new HashMap<>();

        // per ogni tecnologia più utilizzata, ottiene i progetti associati
        mostUsedTechnologies.forEach(technology -> {
            Set<Project> projectsUsingTechnology = employeesWithProjects.stream()
                    .filter(employee -> employee.getTechnologies().contains(technology))
                    .flatMap(employee -> employee.getProjects().stream())
                    .collect(Collectors.toSet());

            // inserisco nella mappa i progetti associati alla tecnologia
            technologyProjectMap.put(technology, projectsUsingTechnology);
        });

        // restituisco la mappa contenente tutte le tecnologie più utilizzate e i progetti associati
        return technologyProjectMap;
    }


//    public Map<Technology, Set<Project>> findMostTechnologies() {
//        // recupero le tecnoogie che sono associate a employee che a loro volta sono associati aalmeno un progetto e cliente
//
//        String queryCheck = "SELECT t.*, COUNT(*) AS usage_count" +
//                " FROM technology t" +
//                " INNER JOIN (" +
//                "    SELECT et.technology_id" +
//                "    FROM client c" +
//                "    INNER JOIN employee_project ep ON c.contact_person_id = ep.employee_id" +
//                "    INNER JOIN employee_technology et ON ep.employee_id = et.employee_id" +
//                " ) AS tech ON t.id = tech.technology_id" +
//                " GROUP BY t.id" +
//                " ORDER BY usage_count DESC";
//
//        // Questa query deve essere una NativeQuery, non una TypedQuery, perché stiamo selezionando più di un'entità
//        List<Object[]> results = entityManager.createNativeQuery(queryCheck)
//                .getResultList();
//
//        Map<Technology, Set<Project>> technologyProjectsMap = new LinkedHashMap<>(); //ho usato la linked per preservare l'ordinamento
//
//        for (Object[] result : results) {
//
//            Technology technology = new Technology();
//            technology.setId((Long) result[0]);
//            technology.setDescription((String) result[1]);
//            technology.setName((String) result[2]);
//            technology.setRequiredExperienceLevel((String) result[3]);
//
//            // recupero i progetti associati agli employee che sono associati a quella tecnologia
//            String queryProject = "select p.*" +
//                    "from technology t " +
//                    "inner join employee_technology et on t.id = et.technology_id " +
//                    "inner join employee_project ep on et.employee_id = ep.employee_id " +
//                    "inner join project p on ep.project_id = p.id " +
//                    "where et.technology_id = :technologyID";
//
//            List<Project> queryProjects = entityManager.createNativeQuery(queryProject, Project.class)
//                    .setParameter("technologyID", technology.getId()).getResultList();
//
//
//            Set<Project> projects = new HashSet<>(queryProjects);
//
//            technologyProjectsMap.put(technology, projects);
//        }
//
//        return technologyProjectsMap;
//    }

}
