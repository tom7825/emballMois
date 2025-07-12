package fr.inventory.packaging.entity.dto.validation;

import fr.inventory.packaging.entity.dto.StockRegistrationDto;

/**
 * DTO used for validation Inventory vue before generating Report and closing it
 */
public class RegistrationValidationDto {

    private Long stockRegistrationId;

    private Boolean isValid;

    private Double quantity;

    private Boolean packagedRegistration;

    public RegistrationValidationDto(Double quantity, Boolean packagingRegistration, Long stockRegistrationId) {
        this.quantity = quantity;
        this.packagedRegistration = packagingRegistration;
        this.stockRegistrationId = stockRegistrationId;
    }

    public RegistrationValidationDto(StockRegistrationDto registration) {
        this.quantity = registration.getQuantity();
        this.packagedRegistration = registration.getPackagingCount();
        this.stockRegistrationId = registration.getStockRegistrationId();
    }

    public Long getStockRegistrationId() {
        return stockRegistrationId;
    }

    public void setStockRegistrationId(Long stockRegistrationId) {
        this.stockRegistrationId = stockRegistrationId;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Boolean getPackagedRegistration() {
        return packagedRegistration;
    }

    public void setPackagedRegistration(Boolean packagedRegistration) {
        this.packagedRegistration = packagedRegistration;
    }
}
