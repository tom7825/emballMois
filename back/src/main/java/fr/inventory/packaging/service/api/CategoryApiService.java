package fr.inventory.packaging.service.api;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.entity.dto.CategoryDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.category.CategoryAlreadyExists;
import fr.inventory.packaging.exceptions.entity.category.CategoryNotFound;

import java.util.List;

/**
 * Service interface for managing categories.
 * This interface defines operations for adding, modifying, retrieving, and searching categories.
 * It supports both active and archived category management.
 */
public interface CategoryApiService {

    /**
     * Adds a new category to the system.
     *
     * @param categoryDto the data transfer object containing the details of the category to be added
     * @return the newly created {@link Category} entity
     * @throws CategoryAlreadyExists  if a category with the same name already exists and is active
     * @throws EntityIllegalParameter if the input data is invalid or null
     */
    ApiResponse<CategoryDto> addCategory(CategoryDto categoryDto) throws CategoryAlreadyExists, EntityIllegalParameter;

    /**
     * Updates an existing category's information.
     *
     * @param categoryDto the data transfer object containing the updated details of the category
     * @return the updated {@link Category} entity
     * @throws EntityIllegalParameter if the input is invalid or null
     * @throws CategoryNotFound       if the category does not exist
     * @throws CategoryAlreadyExists  if the update would duplicate an existing category
     */
    ApiResponse<CategoryDto> modifyCategory(CategoryDto categoryDto) throws EntityIllegalParameter, CategoryNotFound, CategoryAlreadyExists;

    /**
     * Retrieves all active categories (categories without a modification date).
     *
     * @return a list of active {@link Category} entities
     * @throws CategoryNotFound if no active categories are found
     */
    ApiResponse<List<CategoryDto>> getActiveCategories() throws CategoryNotFound;

    /**
     * Retrieves all archived categories (categories marked as inactive or modified).
     *
     * @return a list of archived {@link Category} entities
     * @throws CategoryNotFound if no archived categories are found
     */
    ApiResponse<List<CategoryDto>> getArchivedCategories() throws CategoryNotFound;
}
