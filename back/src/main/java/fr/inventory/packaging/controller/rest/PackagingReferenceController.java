package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.entity.dto.*;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityAlreadyExist;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;
import fr.inventory.packaging.service.api.PackagingReferenceApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/rest/reference")
@CrossOrigin("${front.url}")
public class PackagingReferenceController {

    /// //////////////
    /// Attributs ///
    /// //////////////

    private PackagingReferenceApiService packagingReferenceApiService;


    /// //////////////
    /// Endpoints ///
    /// //////////////

    @GetMapping("/references/active")
    public ResponseEntity<ApiResponse<?>> getAllActiveReferences() {
        try {
            return ResponseEntity.ok().body(packagingReferenceApiService.findAllActiveReference());
        } catch (ReferenceNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/references/archived")
    public ResponseEntity<ApiResponse<?>> getAllArchivedReferences() {
        try {
            return ResponseEntity.ok().body(packagingReferenceApiService.findAllArchivedReference());
        } catch (ReferenceNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/references/all/but/{areaName}")
    public ResponseEntity<ApiResponse<?>> getAllReferencesButInArea(@PathVariable String areaName) {
        try {
            return ResponseEntity.ok().body(packagingReferenceApiService.findAllReferencesExcludingArea(areaName));
        } catch (EntityIllegalParameter | ReferenceNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/references/area/{areaName}")
    public ResponseEntity<ApiResponse<?>> getReferencesByAreaName(@PathVariable String areaName) {
        try {
            return ResponseEntity.ok(packagingReferenceApiService.getActiveReferencesByAreaName(areaName));
        } catch (ReferenceNotFound | AreaNotFound | EntityIllegalParameter e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addNewReference(@RequestBody PackagingReferenceDto packagingReferenceDto) {
        try {
            packagingReferenceApiService.addReference(packagingReferenceDto);
            return ResponseEntity.ok().body(new ApiResponse<>("Référence " + packagingReferenceDto.getReferenceName() + " ajoutée avec succès."));
        } catch (EntityIllegalParameter | EntityAlreadyExist | IncompleteReference e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/add/minimal")
    public ResponseEntity<ApiResponse<?>> addNewMinimalReference(@RequestBody PackagingReferenceDto packagingReferenceDto) {
        try {
            packagingReferenceApiService.addMinimalReference(packagingReferenceDto);
            return ResponseEntity.ok().body(new ApiResponse<>("Référence " + packagingReferenceDto.getReferenceName() + " ajoutée avec succès."));
        } catch (EntityIllegalParameter e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<ApiResponse<?>> modifyReference(@RequestBody PackagingReferenceDto packagingReferenceDto) {
        try {
            packagingReferenceApiService.modifyReference(packagingReferenceDto);

            return ResponseEntity.ok().body(new ApiResponse<>("Référence " + packagingReferenceDto.getReferenceName() + " modifiée avec succès."));
        } catch (EntityIllegalParameter | IncompleteReference e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        } catch (ReferenceNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/{referenceId}/delete/area/{areaName}")
    public ResponseEntity<ApiResponse<?>> deleteAreaOfReference(@PathVariable Long referenceId, @PathVariable String areaName){
        try{
            packagingReferenceApiService.deleteArea(referenceId, areaName);
            return ResponseEntity.ok().body(new ApiResponse<>(areaName + " retiré avec succès de la référence " + referenceId));
        }catch (EntityIllegalParameter e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    /// ////////////
    /// Setters ///
    /// ////////////


    @Autowired
    public void setPackagingReferenceService(PackagingReferenceApiService packagingReferenceApiService) {
        this.packagingReferenceApiService = packagingReferenceApiService;
    }
}
