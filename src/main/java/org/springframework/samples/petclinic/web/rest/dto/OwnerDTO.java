package org.springframework.samples.petclinic.web.rest.dto;

import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Input and output contract of the owner REST API. It keeps the JPA entity out of the public contract, so changes in
 * the domain model no longer ripple into the API.
 */
public class OwnerDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 30, message = "El nombre debe tener entre 1 y 30 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 1, max = 30, message = "El apellido debe tener entre 1 y 30 caracteres")
    private String lastName;

    @NotBlank(message = "La dirección es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "El teléfono es obligatorio")
    @Digits(fraction = 0, integer = 10, message = "El teléfono debe ser un número de hasta 10 dígitos")
    private String telephone;

    /** Only the identifiers of the associated pets, never the full objects. */
    private List<Integer> petIds;

    public OwnerDTO() {
    }

    public OwnerDTO(Integer id, String firstName, String lastName, String address, String city, String telephone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.telephone = telephone;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<Integer> getPetIds() {
        return this.petIds;
    }

    public void setPetIds(List<Integer> petIds) {
        this.petIds = petIds;
    }

}
