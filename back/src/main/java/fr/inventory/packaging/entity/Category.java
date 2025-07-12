package fr.inventory.packaging.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a category entity in the inventory system.
 * This class defines the properties of a category and provides methods to access and modify these properties.
 * It is mapped to a database table using JPA annotations.
 */
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName;

    private Boolean categoryArchived;

    private LocalDateTime modificationDateCategory;

    private Long idMainCategory;

    public Category(String categoryName) {
        this.categoryName = categoryName;
        this.categoryArchived = false;
    }

    public Category(String categoryName, boolean categoryArchived, Long idMainCategory) {
        this.categoryName = categoryName;
        this.categoryArchived = categoryArchived;
        this.idMainCategory = idMainCategory;
    }

    public Category(){}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return categoryArchived == category.categoryArchived &&
                Objects.equals(categoryName, category.categoryName);
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public LocalDateTime getModificationDateCategory() {
        return modificationDateCategory;
    }

    public void setModificationDateCategory(LocalDateTime modificationDateCategory) {
        this.modificationDateCategory = modificationDateCategory;
    }

    public Long getIdMainCategory() {
        return idMainCategory;
    }

    public void setIdMainCategory(Long idMainCategory) {
        this.idMainCategory = idMainCategory;
    }

    public Boolean getCategoryArchived() {
        return categoryArchived;
    }

    public void setCategoryArchived(Boolean categoryArchived) {
        this.categoryArchived = categoryArchived;
    }
}
