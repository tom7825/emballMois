package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.entity.dto.CategoryDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.category.CategoryAlreadyExists;
import fr.inventory.packaging.exceptions.entity.category.CategoryNotFound;
import fr.inventory.packaging.service.api.CategoryApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/rest/category")
@CrossOrigin("${front.url}")
public class CategoryController {

    /// //////////////
    /// Attributs ///
    /// //////////////

    private CategoryApiService categoryApiService;

    /// //////////////
    /// Endpoints ///
    /// //////////////

    @GetMapping("/categories/archived")
    public ResponseEntity<ApiResponse<?>> findArchivedCategories() {
        try {
            return ResponseEntity.ok(categoryApiService.getArchivedCategories());
        } catch (CategoryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/categories/active")
    public ResponseEntity<ApiResponse<?>> findActiveCategories() {
        try {
            return ResponseEntity.ok(categoryApiService.getActiveCategories());
        } catch (CategoryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addNewCategory(@RequestBody CategoryDto categoryDto) {
        try {
            return ResponseEntity.ok().body(categoryApiService.addCategory(categoryDto));
        } catch (EntityIllegalParameter | CategoryAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<ApiResponse<?>> modifyCategory(@RequestBody CategoryDto categoryDto) {
        try {
            return ResponseEntity.ok().body(categoryApiService.modifyCategory(categoryDto));
        } catch (EntityIllegalParameter |CategoryAlreadyExists  e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        } catch (CategoryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    /// ////////////
    /// Setters ///
    /// ////////////

    @Autowired
    public void setCategoryService(CategoryApiService categoryApiService) {
        this.categoryApiService = categoryApiService;
    }
}

