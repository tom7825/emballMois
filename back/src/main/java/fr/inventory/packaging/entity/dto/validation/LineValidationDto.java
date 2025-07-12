package fr.inventory.packaging.entity.dto.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class LineValidationDto {

    private Long referenceId;
    private String referenceName;
    private String calculationRule;
    private Float unitPrice;
    private Integer unitByPackaging;
    private String categoryName;
    private Boolean isValid = true;
    private String unitCount;
    private String storageAreaName;
    private Long storageAreaId;

    @JsonIgnore
    private Boolean isPackaged = false;

    private final Map<String, String> errors = new HashMap<>();
    private final Map<String, String> warnings = new HashMap<>();
    private final List<RegistrationValidationDto> registrations = new ArrayList<>();

    public LineValidationDto(Long referenceId, String referenceName, String calculationRule,
                             Float unitPrice, Integer unitByPackaging, String categoryName, String unitCount) {
        this.referenceId = referenceId;
        this.referenceName = referenceName;
        this.calculationRule = calculationRule;
        this.unitPrice = unitPrice;
        this.unitByPackaging = unitByPackaging;
        this.categoryName = categoryName;
        this.unitCount = unitCount;
    }

    public LineValidationDto() {
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getCalculationRule() {
        return calculationRule;
    }

    public void setCalculationRule(String calculationRule) {
        this.calculationRule = calculationRule;
    }

    public Float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Float unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getUnitByPackaging() {
        return unitByPackaging;
    }

    public void setUnitByPackaging(Integer unitByPackaging) {
        this.unitByPackaging = unitByPackaging;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public List<RegistrationValidationDto> getRegistrations() {
        return registrations;
    }

    public String getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(String unitCount) {
        this.unitCount = unitCount;
    }

    public void addRegistration(RegistrationValidationDto registration) {
        if (registration.getPackagedRegistration() != null && registration.getPackagedRegistration()) {
            isPackaged = true;
        }
        if (registration.getValid() == null || !registration.getValid()) {
            isValid = false;
        }
        this.registrations.add(registration);
    }


    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Boolean getPackaged() {
        return isPackaged;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineValidationDto that)) return false;
        return Objects.equals(referenceId, that.referenceId) &&
                Objects.equals(referenceName, that.referenceName) &&
                Objects.equals(calculationRule, that.calculationRule) &&
                Objects.equals(unitPrice, that.unitPrice) &&
                Objects.equals(unitByPackaging, that.unitByPackaging) &&
                Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId, referenceName, calculationRule, unitPrice, unitByPackaging, categoryName);
    }

    @Override
    public String toString() {
        return "ReferenceValidationDto{" +
                "referenceName='" + referenceName + '\'' +
                ", calculationRule='" + calculationRule + '\'' +
                ", unitPrice=" + unitPrice +
                ", unitByPackaging=" + unitByPackaging +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }

    public void addError(String registration, String error) {
        if (!registration.isEmpty() && !error.isEmpty()) {
            errors.put(registration, error);
            isValid = false;
        }
    }

    public void addErrors(Map<String, String> registrationError) {
        if (!registrationError.isEmpty()) {
            errors.putAll(registrationError);
            isValid = false;
        }
    }

    public void addWarning(String subject, String warning){
        if(!subject.isEmpty() && !warning.isEmpty()) {
            warnings.put(subject, warning);
        }
    }

    public String getStorageAreaName() {
        return storageAreaName;
    }

    public void setStorageAreaName(String storageAreaName) {
        this.storageAreaName = storageAreaName;
    }

    public Long getStorageAreaId() {
        return storageAreaId;
    }

    public void setStorageAreaId(Long storageAreaId) {
        this.storageAreaId = storageAreaId;
    }
}

