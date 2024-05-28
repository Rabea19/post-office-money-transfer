package io.fciz.postoffice.moneytransfer.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link io.fciz.postoffice.moneytransfer.domain.Citizen} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CitizenDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 14, max = 14)
    @Pattern(regexp = "^[0-9]{14}$")
    private String nationalId;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^[0-9]{11}$")
    private String phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CitizenDTO)) {
            return false;
        }

        CitizenDTO citizenDTO = (CitizenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, citizenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CitizenDTO{" +
            "id=" + getId() +
            ", nationalId='" + getNationalId() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            "}";
    }
}
