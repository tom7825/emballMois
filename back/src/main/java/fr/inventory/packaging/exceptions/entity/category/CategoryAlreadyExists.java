package fr.inventory.packaging.exceptions.entity.category;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityAlreadyExist;

/**
 * Exception thrown when an attempt is made to create a category that already exists.
 * This exception is used to handle cases where a category with the same name already exists in the system.
 */
public class CategoryAlreadyExists extends EntityAlreadyExist {

    private final Category existingCategory;

    public CategoryAlreadyExists(Category category, Error error) {
        super(category.getCategoryName(), error);
        existingCategory = category;
    }

    public Category getExistingCategory() {
        return existingCategory;
    }
}
