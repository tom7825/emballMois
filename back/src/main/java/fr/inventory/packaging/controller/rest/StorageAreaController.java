package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.entity.dto.StorageAreaDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaAlreadyExists;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;
import fr.inventory.packaging.service.api.StorageAreaApiService;
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
@RequestMapping("api/rest/area")
@CrossOrigin("${front.url}")
public class StorageAreaController {

    /////////////////
    /// Attributs ///
    /////////////////

    private StorageAreaApiService storageAreaService;

    /////////////////
    /// Endpoints ///
    /////////////////

    @GetMapping("/areas/active")
    public ResponseEntity<ApiResponse<?>> findAllActiveAreas() {
        try {
            return ResponseEntity.ok(storageAreaService.getActiveAreas());
        } catch (AreaNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/areas/active/references/active")
    public ResponseEntity<ApiResponse<?>> findAllActiveAreasWithActiveReferences() {
        try {
            return ResponseEntity.ok(storageAreaService.getActiveAreasWithActivesReferences());
        } catch (AreaNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/areas/archived")
    public ResponseEntity<ApiResponse<?>> findArchivedCategories() {
        try {
            return ResponseEntity.ok(storageAreaService.getArchivedAreas());
        } catch (AreaNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addNewArea(@RequestBody StorageAreaDto storageAreaDto){
        try{
            return ResponseEntity.ok(storageAreaService.addArea(storageAreaDto));
        } catch (EntityIllegalParameter e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }catch (AreaAlreadyExists e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage(),e.getExistingStorageArea()));
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<ApiResponse<?>> modifyArea(@RequestBody StorageAreaDto storageAreaDto){
        try{
            storageAreaService.modifyArea(storageAreaDto);
            return ResponseEntity.ok().body(new ApiResponse<>("La zone " + storageAreaDto.getStorageAreaName() + " à été modifié avec succès."));
        } catch (EntityIllegalParameter | AreaAlreadyExists e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }  catch (AreaNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    ///////////////
    /// Setters ///
    ///////////////

    @Autowired
    public void setStorageAreaService(StorageAreaApiService storageAreaService) {
        this.storageAreaService = storageAreaService;
    }
}

