package fr.inventory.packaging.service.validation.impl;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StockRegistration;
import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.validation.RegistrationValidationDto;
import fr.inventory.packaging.exceptions.registration.IncompleteStockRegistrationException;
import fr.inventory.packaging.service.validation.ReferenceValidator;
import fr.inventory.packaging.service.validation.RegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationValidatorImpl implements RegistrationValidator {

    private ReferenceValidator referenceValidator;

    @Override
    public void validateRegistrationForAddOrModify(StockRegistration registration, PackagingReference reference) throws IncompleteStockRegistrationException {
        Map<String, String> errorFields = new HashMap<>();
        String error;

        error = validateQuantity(registration.getQuantity());
        if (error != null) {
            errorFields.put("quantity", error);
        }
        error = validateReferenceAttached(registration.getReference());
        if (error != null) {
            errorFields.put("reference", error);
        } else {
            errorFields.putAll(referenceValidator.validateReferenceForAddOrModifyStockRegistration(reference, registration.getPackagingCount()));
        }
        if (!errorFields.isEmpty()) {
            throw new IncompleteStockRegistrationException(errorFields, new StockRegistrationDto(registration), new PackagingReferenceDto(reference));
        }
    }


    @Override
    public Map<String, String> validateRegistrationForInventoryValidation(RegistrationValidationDto registration) {
        String error;
        Map<String, String> errors = new HashMap<>();
        registration.setValid(true);

        error = validateQuantity(registration.getQuantity());
        if (error != null) {
            errors.put("quantity", error);
            registration.setValid(false);
        }
        return errors;
    }

    private String validateQuantity(Double quantity) {
        if (quantity == null || quantity < 0) {
            return "Saisie de stock : la quantité doit être positive";
        }
        return null;
    }

    private String validateReferenceAttached(PackagingReference reference) {
        if (reference == null) {
            return "Référence : la saisie doit être rattachée à une référence";
        }
        return null;
    }

    @Autowired
    public void setReferenceValidator(ReferenceValidator referenceValidator) {
        this.referenceValidator = referenceValidator;
    }
}
