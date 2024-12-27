INSERT INTO roles (name) VALUES 
    ('admin'),
    ('project manager');

INSERT INTO users (username, password, role_id) VALUES 
    ('admin', '$2a$10$Uc.SZ0hvGJQlYdsAp7be1.lFjmOnc7aAr4L0YY3/VN3oK.F8zJHRG', 1),
    ('pm', '$2a$10$Uc.SZ0hvGJQlYdsAp7be1.lFjmOnc7aAr4L0YY3/VN3oK.F8zJHRG', 2),
    ('pm2', '$2a$10$Uc.SZ0hvGJQlYdsAp7be1.lFjmOnc7aAr4L0YY3/VN3oK.F8zJHRG', 2);

-- Inserimenti per la tabella "employee"
INSERT INTO employee (name, surname, role, hire_date, salary) VALUES 
    ('John', 'Doe', 'junior', '2023-01-15', 50000.00),
    ('Alice', 'Smith', 'middle', '2023-02-20', 60000.00),
    ('Bob', 'Johnson', 'senior', '2023-03-10', 55000.00);

-- Inserimenti per la tabella "technology"
INSERT INTO technology (name, description, required_experience_level) VALUES 
    ('Java', 'Object-oriented programming language', 'senior'),
    ('Python', 'High-level programming language', 'middle'),
    ('SQL', 'Structured Query Language', 'junior');

-- Inserimenti per la tabella "project"
INSERT INTO project (name, description, start_date, end_date, user_id) VALUES 
    ('Project X', 'Development of new software', '2023-01-01', '2023-06-30', 2),
    ('Project Y', 'Website redesign', '2023-02-15', '2023-08-31', 3),
    ('Project Z', 'Database optimization', '2023-03-20', '2023-10-31', 2);

-- Inserimenti per la tabella "client"
INSERT INTO client (client_name, sector, address, contact_person_id) VALUES 
    ('ABC Company', 'Technology', '123 Main St', 1),
    ('XYZ Corporation', 'Finance', '456 Oak St', 2),
    ('123 Industries', 'Manufacturing', '789 Elm St', 3);
-- Inserimenti per la tabella "employee_project"
INSERT INTO employee_project (employee_id, project_id) VALUES 
    (1, 1), -- John (Project X)
    (2, 2), -- Alice (Project Y)
    (3, 3); -- Bob (Project Z)

-- Inserimenti per la tabella "employee_technology"
INSERT INTO employee_technology (employee_id, technology_id) VALUES 
    (1, 1), -- John (Java)
    (2, 2), -- Alice (Python)
    (3, 3), -- Bob (SQL)
    (3, 1); -- Bob (Java)

