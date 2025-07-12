package fr.inventory.packaging.service.validation.impl;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import fr.inventory.packaging.entity.dto.validation.RegistrationValidationDto;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;
import fr.inventory.packaging.repository.external.CommandeArticleRepository;
import fr.inventory.packaging.service.validation.ReferenceValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReferenceValidatorImpl implements ReferenceValidator {

    private static final Logger logger = LogManager.getLogger();

    private CommandeArticleRepository commandeArticleRepository;

    @Override
    public void validateReference(PackagingReference reference) throws EntityIllegalParameter, IncompleteReference {
        logger.warn("Validate reference : start");
        if (reference == null) {
            logger.warn("Validate reference : stop, parameter is null");
            throw new EntityIllegalParameter("Impossible de valider la référence");
        }
        List<String> missingFields = new ArrayList<>();
        addErrorIfPresent(missingFields, validateReferenceName(reference.getReferenceName()));
        addErrorIfPresent(missingFields, validateCategory(reference.getCategory() != null ? reference.getCategory().getCategoryName() : null));
        addErrorIfPresent(missingFields, validateUnitCount(reference.getUnitCount()));
        addErrorIfPresent(missingFields, validateUnitPrice(reference.getUnitPrice()));
        addErrorIfPresent(missingFields, validateFormula(reference.getCalculationRule()));
        if (reference.getReferenceProductionDB() != null && !reference.getReferenceProductionDB().isEmpty()) {
            addErrorIfPresent(missingFields, validateBddReference(reference.getReferenceProductionDB()));
        }
        if (!missingFields.isEmpty()) {
            logger.error("Validate reference : reference is not valid");
            throw new IncompleteReference(missingFields);
        }
        logger.info("Validate reference : success");
    }

    @Override
    public void validateReference(PackagingReferenceDto reference) throws EntityIllegalParameter, IncompleteReference {
        logger.info("Validate reference DTO : start");
        if (reference == null) {
            logger.warn("Validate reference DTO : stop, parameter is null");
            throw new EntityIllegalParameter("Impossible de valider la référence");
        }
        List<String> missingFields = new ArrayList<>();

        addErrorIfPresent(missingFields, validateReferenceName(reference.getReferenceName()));
        addErrorIfPresent(missingFields, validateCategory(reference.getCategoryName()));
        addErrorIfPresent(missingFields, validateUnitCount(reference.getUnitCount()));
        addErrorIfPresent(missingFields, validateUnitPrice(reference.getUnitPrice()));
        addErrorIfPresent(missingFields, validateFormula(reference.getCalculationRule()));
        if (reference.getReferenceProductionDB() != null && !reference.getReferenceProductionDB().isEmpty()) {
            addErrorIfPresent(missingFields, validateBddReference(reference.getReferenceProductionDB()));
        }
        if (!missingFields.isEmpty()) {
            logger.error("Validate reference DTO : reference is not valid");
            throw new IncompleteReference(missingFields);
        }
        logger.info("Validate reference DTO : success");
    }

    @Override
    public void validateReferenceForInventoryValidation(LineValidationDto lineValidation, Boolean packagingCount) throws EntityIllegalParameter {
        String error;
        if (lineValidation == null) {
            logger.warn("Validate reference for inventory validation : stop, parameter is null");
            throw new EntityIllegalParameter("Impossible de valider la référence");
        }

        error = validateRegistrationExistence(lineValidation.getRegistrations());
        if (error != null) {
            lineValidation.addError("registration", error);
        }
        error = validateUnitPrice(lineValidation.getUnitPrice());
        if (error != null) {
            lineValidation.addError("price", error);
        }
        error = validateCategory(lineValidation.getCategoryName());
        if (error != null) {
            lineValidation.addError("category", error);
        }
        error = validateFormula(lineValidation.getCalculationRule());
        if (error != null) {
            lineValidation.addError("formula", error);
        }
        error = validateUnitByPackaging(lineValidation.getUnitByPackaging(), packagingCount);
        if (error != null) {
            lineValidation.addError("packaging", error);
        }
    }


    @Override
    public Map<String, String> validateReferenceForAddOrModifyStockRegistration(PackagingReference reference, Boolean packagingCount) {
        logger.info("Validate reference for add or Modify registration : start");
        String error;
        Map<String, String> missingFields = new HashMap<>();
        error = validateUnitPrice(reference.getUnitPrice());
        if (error != null) {
            missingFields.put("price", error);
        }
        error = validateCategory(reference.getCategory());
        if (error != null) {
            missingFields.put("category", error);
        }
        error = validateFormula(reference.getCalculationRule());
        if (error != null) {
            missingFields.put("formula", error);
        }
        error = validateUnitByPackaging(reference.getUnitByPackaging(), packagingCount);
        if (error != null) {
            missingFields.put("packaging", error);
        }
        logger.info("Validate reference for add or Modify registration : success");
        return missingFields;
    }


    private String validateReferenceName(String referenceName) {
        return (referenceName == null || referenceName.isEmpty())
                ? "Le nom de la référence ne peut pas être vide"
                : null;
    }

    private String validateBddReference(String bddReference) {
        return (isReferenceExistsInBDD(bddReference)) ? null : "Impossible de retrouver cette référence dans la base EuroFlow";
    }

    private Boolean isReferenceExistsInBDD(String bddReference) {
        return commandeArticleRepository.isArticleExistsRaw(bddReference) != null;
    }

    private String validateRegistrationExistence(List<RegistrationValidationDto> registrations) {
        return registrations.isEmpty() ? "Aucun enregistrement de stock pour cette référence" : null;
    }

    private String validateCategory(String categoryName) {
        return (categoryName == null || categoryName.isEmpty())
                ? "La référence doit être associée à une catégorie"
                : null;
    }

    private String validateCategory(Category category) {
        return (category == null) ? validateCategory("") : validateCategory(category.getCategoryName());
    }

    private String validateUnitCount(String unitCount) {
        return (unitCount == null || unitCount.isEmpty())
                ? "L'Unité de comptage est manquante"
                : null;
    }

    private String validateUnitPrice(Float unitPrice) {
        return (unitPrice == null || unitPrice < 0)
                ? "Le prix unitaire doit être renseigné."
                : null;
    }

    private String validateFormula(String formula) {
        if (formula != null && !formula.isEmpty())
            return (!formula.matches("^([*/]\\d+([.,]\\d+)?){1,2}$")) ? "La formule doit être de la forme [* ou /] + chiffre(s)" : null;
        return null;
    }

    private String validateUnitByPackaging(Integer unitByPackaging, Boolean packagingCount) {
        return (Boolean.TRUE.equals(packagingCount) && (unitByPackaging == null || unitByPackaging <= 0))
                ? "La saisie concerne un produit conditionné, mais le nombre d'unités par conditionnement n'est pas renseigné"
                : null;
    }

    private void addErrorIfPresent(List<String> list, String message) {
        if (message != null) {
            list.add(message);
        }
    }

    @Autowired
    public void setCommandeArticleRepository(CommandeArticleRepository commandeArticleRepository) {
        this.commandeArticleRepository = commandeArticleRepository;
    }
}

