package fr.inventory.packaging.service.core.impl;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.category.CategoryNotFound;
import fr.inventory.packaging.repository.CategoryRepository;
import fr.inventory.packaging.service.core.CategoryService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fr.inventory.packaging.service.utils.Utils.checkNameIsValid;

@Service
public class CategoryServiceImpl implements CategoryService {


    private static final Logger logger = LogManager.getLogger();

    private CategoryRepository categoryRepository;


    @Override
    public Category getActiveCategoryByName(String name) throws CategoryNotFound, EntityIllegalParameter {
        logger.info("Get active category by name: searching for '{}'", name);
        checkNameIsValid(name, "catégorie");
        Category category = categoryRepository.findByCategoryNameAndModificationDateCategoryIsNullAndCategoryArchivedFalse(name);
        if (category == null) {
            logger.warn("Get active category by name: category '{}' not found", name);
            throw new CategoryNotFound(name, Error.ENTITY_NOT_FOUND_ACTIVE);
        }
        logger.info("Get active category by name: category '{}' found", name);
        return category;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
