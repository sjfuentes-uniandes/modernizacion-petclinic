package org.springframework.samples.petclinic.web.rest.mapper;

import java.util.stream.Collectors;

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.web.rest.dto.OwnerDTO;
import org.springframework.stereotype.Component;

/**
 * Converts between the {@link Owner} entity and the {@link OwnerDTO} API contract, keeping that translation out of
 * the controllers.
 */
@Component
public class OwnerMapper {

    public OwnerDTO toDTO(Owner owner) {
        if (owner == null) {
            return null;
        }

        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setFirstName(owner.getFirstName());
        dto.setLastName(owner.getLastName());
        dto.setAddress(owner.getAddress());
        dto.setCity(owner.getCity());
        dto.setTelephone(owner.getTelephone());
        dto.setPetIds(owner.getPets().stream()
            .map(Pet::getId)
            .collect(Collectors.toList()));
        return dto;
    }

    public Owner toEntity(OwnerDTO dto) {
        if (dto == null) {
            return null;
        }

        Owner owner = new Owner();
        copyToEntity(dto, owner);
        return owner;
    }

    /**
     * Copies the editable fields of the DTO onto an existing owner. The pets are associated through the dedicated
     * endpoint, never through the owner payload.
     */
    public void copyToEntity(OwnerDTO dto, Owner owner) {
        owner.setFirstName(dto.getFirstName());
        owner.setLastName(dto.getLastName());
        owner.setAddress(dto.getAddress());
        owner.setCity(dto.getCity());
        owner.setTelephone(dto.getTelephone());
    }

}
