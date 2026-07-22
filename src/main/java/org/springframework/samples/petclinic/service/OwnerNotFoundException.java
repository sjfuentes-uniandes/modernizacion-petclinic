package org.springframework.samples.petclinic.service;

/**
 * Thrown when an <code>Owner</code> requested through the REST API does not exist.
 */
public class OwnerNotFoundException extends RuntimeException {

    public OwnerNotFoundException(String message) {
        super(message);
    }

}
