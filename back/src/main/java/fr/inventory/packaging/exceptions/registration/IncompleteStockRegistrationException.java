package fr.inventory.packaging.exceptions.registration;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;

import java.util.Map;

/**
 * Exception thrown when a stock registration is incomplete.
 * This exception is used to indicate that certain required fields
 * are missing or invalid during the stock registration process.
 * This exception contains information about the missing fields and
 * the related stock registration data.
 */
public class IncompleteStockRegistrationException extends StockRegistrationException {

    private final Map<String,String> missingFields;
    private final StockRegistrationDto stockRegistration;
    private final PackagingReferenceDto packagingReference;

    public IncompleteStockRegistrationException(Map<String,String> missingFields, StockRegistrationDto stockRegistrationDto, PackagingReferenceDto packagingReferenceDto) {
        super("Saisie de stock incomplète, " + missingFields.size() + " erreur(s).");
        this.stockRegistration = stockRegistrationDto;
        this.missingFields = missingFields;
        this.packagingReference = packagingReferenceDto;
    }

    public Map<String,String> getMissingFields() {
        return missingFields;
    }

    public StockRegistrationDto getStockRegistration() {
        return stockRegistration;
    }
    public PackagingReferenceDto getPackagingReference() {
        return packagingReference;
    }
}
