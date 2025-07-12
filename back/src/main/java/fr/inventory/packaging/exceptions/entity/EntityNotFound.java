package fr.inventory.packaging.exceptions.entity;


/**
 * Custom exception thrown when an entity is not found.
 * This exception is used to indicate that a specific entity (either by name or by ID) could not be found.
 */
public abstract class EntityNotFound extends EntityException {

    public EntityNotFound(String entityType){
        super("Aucun.e " + entityType + " trouvé");
    }

    public EntityNotFound(String entityType, String entityParameter, Error errorType){
        super("");
        String error;
        switch (errorType){
            case ENTITY_NOT_FOUND_ACTIVE -> error = ("Aucun.e " + entityType + " active n'existe  avec ce nom : " + entityParameter);
            case ENTITY_NOT_FOUND_ID -> error = ("Aucun.e " + entityType + " active n'existe avec cet identifiant : " + entityParameter);
            case ENTITY_NOT_FOUND_NAME -> error = ("Aucun.e " + entityType + " n'existe avec ce nom : " + entityParameter);
            case ENTITY_NOT_FOUND -> error = ("Aucun " + entityType + " trouvé");
            default -> error = "Erreur par défaut : entité introuvable.";
        }
        setMessage(error);
    }
}
