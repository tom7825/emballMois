package fr.inventory.packaging.exceptions.entity.category;

import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityNotFound;

/**
 * Exception thrown when a category is not found in the system.
 * This exception is used to handle cases where a requested category cannot be found.
 */
public class CategoryNotFound extends EntityNotFound {

    public CategoryNotFound(Long categoryId) {
        super(categoryId.toString(), "Catégorie", Error.ENTITY_NOT_FOUND_ID);
    }


    public CategoryNotFound(String categoryName, Error error){
        super("catégorie",categoryName, error);
    }

    public CategoryNotFound(Error error){
        super("catégorie");
    }
}
