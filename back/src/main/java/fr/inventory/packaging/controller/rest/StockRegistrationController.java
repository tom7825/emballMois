package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.entity.dto.FullRegistrationDataDto;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.inventory.NoInventoryInProgress;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.exceptions.registration.IncompleteStockRegistrationException;
import fr.inventory.packaging.service.api.StockRegistrationApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/rest/registration")
@CrossOrigin("${front.url}")
public class StockRegistrationController {

    /// //////////////
    /// Attributs ///
    /// //////////////

    private StockRegistrationApiService stockRegistrationApiService;

    /// //////////////
    /// Endpoints ///
    /// //////////////

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addRegistration(@RequestBody StockRegistrationDto stockRegistrationDto) {
        try {
            return ResponseEntity.ok().body(stockRegistrationApiService.addStockRegistration(stockRegistrationDto));
        } catch (EntityIllegalParameter | NoInventoryInProgress e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        } catch (IncompleteStockRegistrationException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage(),
                    e.getMissingFields(),
                    new FullRegistrationDataDto(e.getPackagingReference(), e.getStockRegistration())));
        }
    }

    @PostMapping("/add/without/verification")
    public ResponseEntity<?> addRegistrationWithoutVerification(@RequestBody StockRegistrationDto stockRegistrationDto) {
        try {
            stockRegistrationApiService.addStockRegistrationWithoutVerification(stockRegistrationDto);
            return ResponseEntity.ok().build();
        } catch (EntityIllegalParameter | NoInventoryInProgress e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }


    @GetMapping("/allStockRegistration/inventory/{idInventory}")
    public ResponseEntity<ApiResponse<?>> getRegistrationsFromInventory(@PathVariable Long idInventory) {
        try {
            return ResponseEntity.ok().body(stockRegistrationApiService.getRegistrationsDtoFromInventory(idInventory));
        } catch (EntityIllegalParameter | StockRegistrationsNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/registration/{idRegistration}")
    public ResponseEntity<ApiResponse<?>> getRegistrationsById(@PathVariable Long idRegistration) {
        try {
            return ResponseEntity.ok().body(stockRegistrationApiService.getRegistrationsDtoById(idRegistration));
        } catch (EntityIllegalParameter | StockRegistrationsNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/allStockRegistration/inventory/{idInventory}/validation")
    public ResponseEntity<ApiResponse<?>> getRegistrationsForValidationFromInventory(@PathVariable Long idInventory) {
        try {
            return ResponseEntity.ok().body(stockRegistrationApiService.getRegistrationsValidationFromInventory(idInventory));
        } catch (EntityIllegalParameter | StockRegistrationsNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/lastStockRegistration")
    public ResponseEntity<ApiResponse<String>> addToCurrentInventoryLastExistingRegistrationsForAreaNameAndReferenceNam(@RequestParam String areaName, @RequestParam String referenceName) {
        try {
            stockRegistrationApiService.addLastExistingRegistrationsToCurrentInventory(areaName, referenceName);
            return ResponseEntity.ok().body(new ApiResponse<>("Les derniers enregistrements de stock connus pour la référence " + referenceName +
                    " dans la zone " + areaName + " ont été ajoutés à l'inventaire en cours"));
        } catch (StockRegistrationsNotFound e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/lastStockRegistration/all")
    public ResponseEntity<ApiResponse<String>> addToCurrentInventoryLastExistingRegistrationsForAllReferencesInAreaName(@RequestParam String areaName) {
        Integer referenceUpdatedCount = stockRegistrationApiService.addLastExistingRegistrationsForAllReferencesToCurrentInventory(areaName);
        if (referenceUpdatedCount != null && referenceUpdatedCount > 0) {
            return ResponseEntity.ok().body(new ApiResponse<>("Les derniers enregistrements de stock connus pour " + referenceUpdatedCount + " références " +
                    " dans la zone " + areaName + " ont été ajoutés à l'inventaire en cours"));
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>("Aucun enregistrement de stock trouvé dans les inventaires précédents"));
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<ApiResponse<?>> modifyStockRegistration(@RequestBody StockRegistrationDto stockRegistrationDto) {
        try {
            stockRegistrationApiService.updateStockRegistration(stockRegistrationDto);
            return ResponseEntity.ok().body(new ApiResponse<>("L'enregistrement de stock pour la référence " + stockRegistrationDto.getReferenceName() +
                    " dans la zone " + stockRegistrationDto.getStorageAreaName() + " a été modifié avec succès."));
        } catch (EntityIllegalParameter | StockRegistrationsNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        } catch (IncompleteStockRegistrationException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(
                    e.getMessage(),
                    e.getMissingFields(),
                    new FullRegistrationDataDto(e.getPackagingReference(), e.getStockRegistration())));
        }
    }


    /// ////////////
    /// Setters ///
    /// ////////////

    @Autowired
    public void setStockRegistrationService(StockRegistrationApiService stockRegistrationApiService) {
        this.stockRegistrationApiService = stockRegistrationApiService;
    }
}
