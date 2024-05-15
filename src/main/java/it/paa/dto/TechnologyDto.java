package it.paa.dto;

import it.paa.validation.ValidExperience;
import jakarta.validation.constraints.NotBlank;

public class TechnologyDto {
    @NotBlank(message = "Nome cannot be empty")
    private String name;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @ValidExperience//validatore per l'esperienza'
    private String requiredExperienceLevel;

    public TechnologyDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequiredExperienceLevel() {
        return requiredExperienceLevel;
    }

    public void setRequiredExperienceLevel(String requiredExperienceLevel) {
        this.requiredExperienceLevel = requiredExperienceLevel;
    }

    @Override
    public String toString() {
        return "TechnologyDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", requiredExperienceLevel='" + requiredExperienceLevel + '\'' +
                '}';
    }
}
