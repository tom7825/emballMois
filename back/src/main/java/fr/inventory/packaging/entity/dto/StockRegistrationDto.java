package fr.inventory.packaging.entity.dto;

import fr.inventory.packaging.entity.StockRegistration;

/**
 * Data Transfer Object (DTO) for StockRegistration.
 * This class is used to transfer stock registration data between the backend and the frontend.
 */
public class StockRegistrationDto {

    private Long stockRegistrationId;

    private Double quantity;

    private String comment;

    private String referenceName;

    private Long idReference;

    private String storageAreaName;

    private Long storageAreaId;

    private String categoryName;

    private Boolean packagingCount;

    private Integer unitByPackaging;

    public StockRegistrationDto(StockRegistration stockRegistration) {
        this.stockRegistrationId = stockRegistration.getIdStockRegistration();
        this.quantity = stockRegistration.getQuantity();
        this.comment = stockRegistration.getComment();
        this.referenceName = stockRegistration.getReference().getReferenceName();
        this.idReference = stockRegistration.getReference().getIdReference();
        this.storageAreaName = stockRegistration.getStorageArea().getStorageAreaName();
        this.storageAreaId = stockRegistration.getStorageArea().getStorageAreaId();
        this.packagingCount =stockRegistration.getPackagingCount();
        if(stockRegistration.getReference().getCategory() != null) {
            this.categoryName = stockRegistration.getReference().getCategory().getCategoryName();
        }
    }

    public StockRegistrationDto(Long stockRegistrationId, Double quantity, String comment, String referenceName, Long idReference, String storageAreaName, Long storageAreaId, String categoryName, Boolean packagingCount, Integer unitByPackaging) {
        this.stockRegistrationId = stockRegistrationId;
        this.quantity = quantity;
        this.comment = comment;
        this.referenceName = referenceName;
        this.idReference = idReference;
        this.storageAreaName = storageAreaName;
        this.storageAreaId = storageAreaId;
        this.packagingCount = packagingCount;
        this.categoryName = categoryName;
        this.unitByPackaging = unitByPackaging;
    }

    public StockRegistrationDto() {

    }


    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getStorageAreaName() {
        return storageAreaName;
    }

    public void setStorageAreaName(String storageAreaName) {
        this.storageAreaName = storageAreaName;
    }

    public Boolean getPackagingCount() {
        return packagingCount;
    }

    public void setPackagingCount(Boolean packagingCount) {
        this.packagingCount = packagingCount;
    }

    public Long getIdReference() {
        return idReference;
    }

    public void setIdReference(Long idReference) {
        this.idReference = idReference;
    }

    public Long getStorageAreaId() {
        return storageAreaId;
    }

    public void setStorageAreaId(Long storageAreaId) {
        this.storageAreaId = storageAreaId;
    }

    public Long getStockRegistrationId() {
        return stockRegistrationId;
    }

    public void setStockRegistrationId(Long stockRegistrationId) {
        this.stockRegistrationId = stockRegistrationId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getUnitByPackaging() {
        return unitByPackaging;
    }

    public void setUnitByPackaging(Integer unitByPackaging) {
        this.unitByPackaging = unitByPackaging;
    }
}
