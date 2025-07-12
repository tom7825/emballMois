package fr.inventory.packaging.exceptions.entity;

/**
 * Exception thrown when an entity cannot be registered because it already exists.
 * This class extends the {@link EntityException} class.
 *
 */
public class EntityAlreadyExist extends EntityException {

    public EntityAlreadyExist(String entityName, Error error) {
        super("");
        switch (error){
            case ENTITY_ALREADY_EXISTS_CREATE -> setMessage(entityName + " ne peut pas être enregistré, le nom existe déjà.");
            case ENTITY_ALREADY_EXISTS_MODIFY -> setMessage(entityName + " n'a pas été modifié, aucun paramètre n'a changé.");
        }
    }


}
