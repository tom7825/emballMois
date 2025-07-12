package fr.inventory.packaging.entity.dto;

/**
 * Data Transfer Object (DTO) for Category.
 * This class is used to transfer category-related data between the backend and the frontend
 * without exposing the entire entity.
 */
public class CategoryDto {

    private Long categoryId;

    private String categoryName;

    private Boolean categoryArchived;

    public CategoryDto() {
    }

    public CategoryDto(Long categoryId, String categoryName, Boolean categoryArchived) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryArchived = categoryArchived;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isCategoryArchived() {
        return categoryArchived;
    }

    public void setCategoryArchived(boolean categoryArchived) {
        this.categoryArchived = categoryArchived;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}

