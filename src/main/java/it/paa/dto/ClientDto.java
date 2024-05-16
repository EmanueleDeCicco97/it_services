package it.paa.dto;

import jakarta.validation.constraints.NotBlank;

public class ClientDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Sector cannot be empty")
    private String sector;

    private String address;

    private Long employeeId;

    public ClientDto() {
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

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "name='" + name + '\'' +
                ", sector='" + sector + '\'' +
                ", address='" + address + '\'' +
                ", employeeId=" + employeeId +
                '}';
    }
}
