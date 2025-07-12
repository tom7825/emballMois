package fr.inventory.packaging.service.core;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.category.CategoryNotFound;

/**
 * Core service interface for managing packaging categories.
 */
public interface CategoryService {

    /**
     * Retrieves an active category by its name.
     *
     * @param categoryName the name of the category to look up (must not be null)
     * @return the active {@link Category} matching the given name
     * @throws CategoryNotFound if no active category is found with the specified name
     * @throws EntityIllegalParameter if the input name is null or invalid
     */
    Category getActiveCategoryByName(String categoryName) throws CategoryNotFound, EntityIllegalParameter;
}
