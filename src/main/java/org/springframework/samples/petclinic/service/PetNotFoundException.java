package org.springframework.samples.petclinic.service;

/**
 * Thrown when a <code>Pet</code> requested through the REST API does not exist.
 */
public class PetNotFoundException extends RuntimeException {

    public PetNotFoundException(String message) {
        super(message);
    }

}
