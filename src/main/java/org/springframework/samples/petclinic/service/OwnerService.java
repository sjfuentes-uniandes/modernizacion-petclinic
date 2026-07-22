package org.springframework.samples.petclinic.service;

import java.util.Collection;

import jakarta.persistence.NoResultException;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dedicated to owner management, extracted from the {@link ClinicServiceImpl} facade so that the owner
 * business logic has a single responsibility. It reuses the existing repositories, so the REST API and the legacy
 * JSP channel work on the same data.
 */
@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    public OwnerService(OwnerRepository ownerRepository, PetRepository petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    @Transactional(readOnly = true)
    public Owner findOwnerById(Integer id) {
        Owner owner;
        try {
            owner = this.ownerRepository.findById(id);
        } catch (NoResultException | DataAccessException ex) {
            // each repository implementation reports a missing row differently
            throw new OwnerNotFoundException("Owner con ID " + id + " no encontrado");
        }
        if (owner == null) {
            throw new OwnerNotFoundException("Owner con ID " + id + " no encontrado");
        }
        return owner;
    }

    @Transactional(readOnly = true)
    public Collection<Owner> findOwnerByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return findAllOwners();
        }
        return this.ownerRepository.findByLastName(lastName);
    }

    @Transactional(readOnly = true)
    public Collection<Owner> findAllOwners() {
        return this.ownerRepository.findAll();
    }

    /**
     * Saves the given owner, either inserting or updating it. The generated identifier is written back onto the
     * supplied instance by every repository implementation, so the returned owner always carries its id.
     */
    @Transactional
    public Owner saveOwner(Owner owner) {
        this.ownerRepository.save(owner);
        return owner;
    }

    /**
     * Links an existing pet with an existing owner. Both records are checked before the association is created.
     */
    @Transactional
    public void associatePetWithOwner(Integer ownerId, Integer petId) {
        Owner owner = findOwnerById(ownerId);
        Pet pet = findPetById(petId);
        owner.addPet(pet);
        this.ownerRepository.save(owner);
    }

    private Pet findPetById(Integer petId) {
        Pet pet;
        try {
            pet = this.petRepository.findById(petId);
        } catch (NoResultException | DataAccessException ex) {
            throw new PetNotFoundException("Pet con ID " + petId + " no encontrado");
        }
        if (pet == null) {
            throw new PetNotFoundException("Pet con ID " + petId + " no encontrado");
        }
        return pet;
    }

}
