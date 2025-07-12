package fr.inventory.packaging.repository;

import fr.inventory.packaging.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing {@link Category} entities.
 * This interface extends {@link JpaRepository} and provides methods for accessing and manipulating {@link Category} entities in the database.
 */
public interface CategoryRepository extends JpaRepository<Category,Long> {

    /**
     * Retrieves a category by its name, where the modification date is null (indicating it is the last one).
     *
     * @param name the name of the category to search for
     * @return the active category with the specified name, or null if no such category exists
     */
    Category findByCategoryNameAndModificationDateCategoryIsNull(String name);

    /**
     * Retrieves all active (non-archived) categories ordered by their ID in descending order.
     *
     * @return a list of non-archived categories, ordered by category ID in descending order
     */
    List<Category> findAllByModificationDateCategoryIsNullAndCategoryArchivedIsFalseOrderByCategoryIdDesc();

    /**
     * Retrieves a category by its name, where the modification date is null and the category is not archived.
     *
     * @param name the name of the category to search for
     * @return the active and non-archived category with the specified name, or null if no such category exists
     */
    Category findByCategoryNameAndModificationDateCategoryIsNullAndCategoryArchivedFalse(String name);

    /**
     * Retrieves all archived categories ordered by their ID in descending order.
     *
     * @return a list of archived categories, ordered by category ID in descending order
     */
    List<Category> findAllByModificationDateCategoryIsNullAndCategoryArchivedIsTrueOrderByCategoryIdDesc();
}
