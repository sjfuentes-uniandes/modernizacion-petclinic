package org.springframework.samples.petclinic.web.rest;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.web.rest.dto.OwnerDTO;
import org.springframework.samples.petclinic.web.rest.mapper.OwnerMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST interface for owner management (wrapping strategy). It exposes the existing business logic through DTOs and
 * coexists with the legacy {@link org.springframework.samples.petclinic.web.OwnerController} MVC controller: both
 * share the same service, repository and database.
 */
@RestController
@RequestMapping("/api/owners")
public class OwnerRestController {

    private final OwnerService ownerService;
    private final OwnerMapper ownerMapper;

    public OwnerRestController(OwnerService ownerService, OwnerMapper ownerMapper) {
        this.ownerService = ownerService;
        this.ownerMapper = ownerMapper;
    }

    /** GET /api/owners?lastName=... - search by last name, or list every owner when the parameter is absent. */
    @GetMapping
    public ResponseEntity<List<OwnerDTO>> getOwners(
            @RequestParam(name = "lastName", required = false) String lastName) {
        Collection<Owner> owners = this.ownerService.findOwnerByLastName(lastName);
        List<OwnerDTO> dtos = owners.stream()
            .map(this.ownerMapper::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** GET /api/owners/{id} - retrieve a single owner. */
    @GetMapping("/{id}")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Integer id) {
        return ResponseEntity.ok(this.ownerMapper.toDTO(this.ownerService.findOwnerById(id)));
    }

    /** POST /api/owners - create an owner. */
    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody OwnerDTO ownerDTO) {
        Owner saved = this.ownerService.saveOwner(this.ownerMapper.toEntity(ownerDTO));
        OwnerDTO body = this.ownerMapper.toDTO(saved);
        return ResponseEntity.created(URI.create("/api/owners/" + body.getId())).body(body);
    }

    /** PUT /api/owners/{id} - update an existing owner; it never creates one. */
    @PutMapping("/{id}")
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable Integer id, @Valid @RequestBody OwnerDTO ownerDTO) {
        Owner existing = this.ownerService.findOwnerById(id);
        this.ownerMapper.copyToEntity(ownerDTO, existing);
        return ResponseEntity.ok(this.ownerMapper.toDTO(this.ownerService.saveOwner(existing)));
    }

    /** POST /api/owners/{ownerId}/pets/{petId} - link an existing pet with an existing owner. */
    @PostMapping("/{ownerId}/pets/{petId}")
    public ResponseEntity<OwnerDTO> associatePet(@PathVariable Integer ownerId, @PathVariable Integer petId) {
        this.ownerService.associatePetWithOwner(ownerId, petId);
        return ResponseEntity.ok(this.ownerMapper.toDTO(this.ownerService.findOwnerById(ownerId)));
    }

}
