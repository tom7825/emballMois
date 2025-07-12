package fr.inventory.packaging.exceptions.entity.reference;

import fr.inventory.packaging.exceptions.entity.EntityException;

import java.util.List;

/**
 * Exception thrown when a reference is incomplete.
 * This exception is used when required fields for a reference are missing or incomplete.
 * It provides a detailed message about which fields are missing from the reference.
 * It extends from `EntityException` to handle errors related to incomplete entities.
 */
public class IncompleteReference extends EntityException {
    public IncompleteReference(List<String> missingFields) {
        super("Référence incomplète : " + String.join(" - ", missingFields));
    }
}
