package fr.inventory.packaging.service.api.impl;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.entity.dto.CategoryDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.category.CategoryAlreadyExists;
import fr.inventory.packaging.exceptions.entity.category.CategoryNotFound;
import fr.inventory.packaging.repository.CategoryRepository;
import fr.inventory.packaging.service.api.CategoryApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static fr.inventory.packaging.service.utils.Utils.checkNameIsValid;

@Service
public class CategoryApiServiceImpl implements CategoryApiService {


    private static final Logger logger = LogManager.getLogger();

    private CategoryRepository categoryRepository;

    @Override
    public ApiResponse<CategoryDto> addCategory(CategoryDto categoryDto) throws CategoryAlreadyExists, EntityIllegalParameter {
        logger.info("Add category: start process for category name '{}'", categoryDto != null ? categoryDto.getCategoryName() : "null");
        isDtoValid(categoryDto);
        try {
            Category category = getCurrentCategoryByName(categoryDto.getCategoryName());
            logger.warn("Add category: category '{}' already exists, creation aborted", categoryDto.getCategoryName());
            throw new CategoryAlreadyExists(category, Error.ENTITY_ALREADY_EXISTS_CREATE);
        } catch (CategoryNotFound e) {
            logger.info("Add category: category '{}' does not exist, proceeding to save", categoryDto.getCategoryName());
            return new ApiResponse<>(
                    "Nouvelle catégorie, " + categoryDto.getCategoryName() + ", enregistrée.",
                    convertCategoryToDto(categoryRepository.save(new Category(categoryDto.getCategoryName())))
            );
        }
    }

    @Override
    public ApiResponse<CategoryDto> modifyCategory(CategoryDto categoryDto) throws EntityIllegalParameter, CategoryNotFound, CategoryAlreadyExists {
        logger.info("Modify category: start process for category name '{}'", categoryDto != null ? categoryDto.getCategoryName() : "null");

        isDtoValid(categoryDto);

        Category savedCategory = getCurrentCategoryByName(categoryDto.getCategoryName());
        Category newCategory = new Category(categoryDto.getCategoryName(), categoryDto.isCategoryArchived(), savedCategory.getCategoryId());
        if (savedCategory.equals(newCategory)) {
            logger.info("Modify category: no changes detected for '{}', update skipped", savedCategory.getCategoryName());
            throw new CategoryAlreadyExists(newCategory, Error.ENTITY_ALREADY_EXISTS_MODIFY);
        }

        savedCategory.setModificationDateCategory(LocalDateTime.now());
        logger.info("Modify category: category '{}' archived at {}", savedCategory.getCategoryName(), savedCategory.getModificationDateCategory());
        categoryRepository.save(savedCategory);
        Category createdCategory = categoryRepository.save(newCategory);
        logger.info("Modify category: new version of category '{}' created", createdCategory.getCategoryName());
        return new ApiResponse<>("Catégorie " + createdCategory.getCategoryName() + " modifiée avec succès.",
                convertCategoryToDto(createdCategory));
    }

    @Override
    public ApiResponse<List<CategoryDto>> getActiveCategories() throws CategoryNotFound {
        logger.info("Get active categories: fetching all non-archived and unmodified categories");
        List<Category> categories = categoryRepository.findAllByModificationDateCategoryIsNullAndCategoryArchivedIsFalseOrderByCategoryIdDesc();
        if (categories.isEmpty()) {
            logger.warn("Get active categories: no active categories found");
            throw new CategoryNotFound(Error.ENTITY_NOT_FOUND);
        }
        logger.info("Get active categories: {} active categories found", categories.size());
        return new ApiResponse<>(categories.size() + " catégories trouvées.", convertCategoriesToDto(categories));
    }

    @Override
    public ApiResponse<List<CategoryDto>> getArchivedCategories() throws CategoryNotFound {
        logger.info("Get archived categories: fetching all archived and unmodified categories");
        List<Category> categories = categoryRepository.findAllByModificationDateCategoryIsNullAndCategoryArchivedIsTrueOrderByCategoryIdDesc();
        if (categories.isEmpty()) {
            logger.warn("Get archived categories: no archived categories found");
            throw new CategoryNotFound(Error.ENTITY_NOT_FOUND);
        }
        logger.info("Get archived categories: {} archived categories found", categories.size());
        return new ApiResponse<>(categories.size() + " catégories trouvées.", convertCategoriesToDto(categories));
    }


    private Category getCurrentCategoryByName(String name) throws CategoryNotFound, EntityIllegalParameter {
        logger.info("Get current category by name: looking up category '{}'", name);
        checkNameIsValid(name, "catégorie");
        Category category = categoryRepository.findByCategoryNameAndModificationDateCategoryIsNull(name);
        if (category == null) {
            logger.warn("Get current category by name: category '{}' not found", name);
            throw new CategoryNotFound(name, Error.ENTITY_NOT_FOUND_ACTIVE);
        }
        logger.info("Get current category by name: category '{}' found", name);
        return category;
    }

    private List<CategoryDto> convertCategoriesToDto(List<Category> categories) {
        return categories.stream()
                .map(category -> new CategoryDto(category.getCategoryId(), category.getCategoryName(), category.getCategoryArchived()))
                .collect(Collectors.toList());
    }

    private CategoryDto convertCategoryToDto(Category category) {
        return new CategoryDto(category.getCategoryId(), category.getCategoryName(), category.getCategoryArchived());
    }

    private void isDtoValid(CategoryDto dto) throws EntityIllegalParameter {
        if (dto == null) {
            logger.error("Error : input CategoryDto is null");
            throw new EntityIllegalParameter("Les données de la catégorie sont invalides");
        }
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
