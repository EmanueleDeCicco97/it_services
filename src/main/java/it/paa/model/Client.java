package it.paa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

// i validator li ho messi sul dto
@Entity
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false)
    @NotEmpty(message = "Client name cannot be empty")
    private String name;

    @Column(name = "sector", nullable = false)
    @NotEmpty(message = "sector cannot be empty")
    private String sector;

    @Column(name = "address")
    @NotEmpty(message = "Address cannot be empty")
    private String address;


    @ManyToOne
    @JoinColumn(name = "contact_person_id")
    @JsonBackReference
    private Employee contactPerson;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Employee getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Employee contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                ", address='" + address + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                '}';
    }
}
