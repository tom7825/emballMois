package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.inventory.InventoryAlreadyExists;
import fr.inventory.packaging.exceptions.inventory.NoInventoryFound;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.service.api.InventoryApiService;
import fr.inventory.packaging.service.validation.InventoryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/rest/inventory")
@CrossOrigin("${front.url}")
public class InventoryController {

    /// //////////////
    /// Attributs ///
    /// //////////////

    private InventoryApiService inventoryApiService;

    private InventoryValidator inventoryValidator;

    /// //////////////
    /// Endpoints ///
    /// //////////////

    @GetMapping("/create")
    public ResponseEntity<ApiResponse<String>> createInventory(){
        try {
            inventoryApiService.createInventory();
            return ResponseEntity.ok().body(new ApiResponse<>("Inventaire démarré"));
        } catch (InventoryAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/inventories")
    public ResponseEntity<ApiResponse<?>> getAllInventories(){
        try {
            return ResponseEntity.ok().body(inventoryApiService.getAllCloseInventories());
        } catch (NoInventoryFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/end")
    public ResponseEntity<ApiResponse<String>> closeInventory(){
        try {
            inventoryApiService.endInventory();
            return ResponseEntity.ok().body(new ApiResponse<>("Inventaire cloturé avec succès"));
        } catch (NoInventoryInProgress e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<?>> isInventoryInProgress(){
        try{
            return ResponseEntity.ok(inventoryApiService.inventoryInProgress());
        }catch (NoInventoryInProgress e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/validation/inventory/{idInventory}")
    public ResponseEntity<ApiResponse<?>> getInventoryValidated(@PathVariable Long idInventory){
        try {
            return ResponseEntity.ok(inventoryValidator.validateInventory(idInventory));
        } catch (StockRegistrationsNotFound | EntityIllegalParameter e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    /// ////////////
    /// Setters ///
    /// ////////////

    @Autowired
    public void setInventoryService(InventoryApiService inventoryApiService) {
        this.inventoryApiService = inventoryApiService;
    }

    @Autowired
    public void setInventoryValidator(InventoryValidator inventoryValidator) {
        this.inventoryValidator = inventoryValidator;
    }
}
