package fr.inventory.packaging.service.validation.impl;

import fr.inventory.packaging.entity.dto.AreaReferenceDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import fr.inventory.packaging.entity.dto.validation.RegistrationValidationDto;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.service.core.PackagingReferenceService;
import fr.inventory.packaging.service.core.StockRegistrationService;
import fr.inventory.packaging.service.validation.InventoryValidator;
import fr.inventory.packaging.service.validation.ReferenceValidator;
import fr.inventory.packaging.service.validation.RegistrationValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryValidatorImpl implements InventoryValidator {

    private static final Logger logger = LogManager.getLogger();

    private StockRegistrationService stockRegistrationService;

    private PackagingReferenceService packagingReferenceService;

    private RegistrationValidator registrationValidator;

    private ReferenceValidator referenceValidator;



    @Override
    public ApiResponse<Map<String, List<LineValidationDto>>> validateInventory(Long inventoryId) throws EntityIllegalParameter, StockRegistrationsNotFound {
        logger.info("Starting inventory validation for inventory ID: {}", inventoryId);
        if(inventoryId == null){
            throw new EntityIllegalParameter("Impossible de valider l'inventaire, paramètre vide");
        }
        Map<String, List<LineValidationDto>> validationStructure = buildEmptyValidationStructure();
        List<StockRegistrationDto> stockRegistrations = stockRegistrationService.getRegistrationsDtoFromInventory(inventoryId);

        logger.info("Found {} stock registrations to process.", stockRegistrations.size());
        for (StockRegistrationDto registration : stockRegistrations) {
            String areaName = registration.getStorageAreaName();
            Long areaId = registration.getStorageAreaId();
            RegistrationValidationDto registrationValidationDto = new RegistrationValidationDto(registration);
            validationStructure.computeIfAbsent(areaName, a -> new ArrayList<>());
            addValidationLineToMap(registrationValidationDto, validationStructure.get(areaName), registration.getIdReference(), areaId, areaName);
        }
        for (String s : validationStructure.keySet()) {
            for (LineValidationDto lineValidationDto : validationStructure.get(s)) {
                referenceValidator.validateReferenceForInventoryValidation(lineValidationDto, lineValidationDto.getPackaged());
            }
        }
        logger.info("Inventory validation completed.");
        return new ApiResponse<>("Structure de validation créée et complétée avec succès.", validationStructure);
    }

    /**
     * Adds a validation line to the correct reference entry within the specified area.
     * If the reference is not found, it is retrieved from the database and added to the structure.
     *
     * @param registration   the registration line to add
     * @param refList the reference-to-lines map for the current area
     */
    private void addValidationLineToMap(RegistrationValidationDto registration, List<LineValidationDto> refList, Long referenceId, Long areaId, String areaName) {
        Optional<LineValidationDto> existingLineValidation = refList.stream()
                .filter(entry -> entry.getReferenceId().equals(referenceId)).findFirst();

        if (existingLineValidation.isPresent()) {
            ValidateAndAddRegistrationToValidationLine(registration,existingLineValidation.get());
        } else {
            logger.debug("Reference not found in structure, loading from DB: {}", referenceId);
            LineValidationDto newLineValidation = packagingReferenceService.findReferenceValidationById(referenceId);
            if(newLineValidation != null) {
                newLineValidation.setStorageAreaId(areaId);
                newLineValidation.setStorageAreaName(areaName);
                ValidateAndAddRegistrationToValidationLine(registration, newLineValidation);
                refList.add(newLineValidation);
            }
        }
    }


    private void ValidateAndAddRegistrationToValidationLine(RegistrationValidationDto registration, LineValidationDto line){
        Map<String, String> registrationErrors = registrationValidator.validateRegistrationForInventoryValidation(registration);
        line.addErrors(registrationErrors);
        line.addRegistration(registration);
    }

    /**
     * Builds an initial validation structure by retrieving all active references grouped by storage area.
     * Each reference is mapped to an empty list of validation lines.
     *
     * @return the initialized map structure for validation
     */
    private Map<String, List<LineValidationDto>> buildEmptyValidationStructure() {
        Map<String, List<LineValidationDto>> structure = new HashMap<>();
        logger.info("Initializing validation structure: adding references to areas");
        for (AreaReferenceDto dto : packagingReferenceService.findAllActiveReferencesNameByAreaName()) {
            logger.debug("Add reference : {} in area : {}", dto.reference().getReferenceName(), dto.areaName());
            structure.computeIfAbsent(dto.areaName(),k-> new ArrayList<>()).add(dto.reference());
        }
        return structure;
    }

    @Autowired
    public void setStockRegistrationService(StockRegistrationService stockRegistrationService) {
        this.stockRegistrationService = stockRegistrationService;
    }

    @Autowired
    public void setPackagingReferenceService(PackagingReferenceService packagingReferenceService) {
        this.packagingReferenceService = packagingReferenceService;
    }

    @Autowired
    public void setRegistrationValidator(RegistrationValidator registrationValidator) {
        this.registrationValidator = registrationValidator;
    }

    @Autowired
    public void setReferenceValidator(ReferenceValidator referenceValidator) {
        this.referenceValidator = referenceValidator;
    }
}
