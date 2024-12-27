DROP TABLE IF EXISTS employee_project;
DROP TABLE IF EXISTS employee_technology;
DROP TABLE IF EXISTS client;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS technology;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT REFERENCES roles(id)
);

CREATE TABLE employee (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    hire_date DATE,
    salary DOUBLE PRECISION
);

CREATE TABLE technology (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    required_experience_level VARCHAR(255)
);

CREATE TABLE project (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    start_date DATE,
    end_date DATE,
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE client (
    id SERIAL PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL,
    sector VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    contact_person_id BIGINT,
    FOREIGN KEY (contact_person_id) REFERENCES employee(id)
);

CREATE TABLE employee_technology (
    employee_id BIGINT,
    technology_id BIGINT,
    PRIMARY KEY (employee_id, technology_id),
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (technology_id) REFERENCES technology(id)
);

CREATE TABLE employee_project (
    employee_id BIGINT,
    project_id BIGINT,
    PRIMARY KEY (employee_id, project_id),
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (project_id) REFERENCES project(id)
);
