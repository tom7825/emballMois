package fr.inventory.packaging.entity.dto;

public class StockRegistrationForReportDto {

    private final Long stockRegistrationId;

    private final Double quantity;

    private final String comment;

    private final String referenceName;

    private final Long idReference;

    private final String storageAreaName;

    private final Long storageAreaId;

    private final Boolean packagingCount;

    private final String categoryName;

    private final Long categoryId;

    private final String supplierName;

    private final String unitCount;

    private final Float unitPrice;

    private final String calculationRule;

    private final Integer unitByPackaging;

    private final Integer refFactPrice;

    public StockRegistrationForReportDto(Long stockRegistrationId, Double quantity, String comment, String referenceName, Long idReference, String storageAreaName, Long storageAreaId, Boolean packagingCount, String categoryName, Long categoryId, String supplierName, String unitCount, Float unitPrice, String calculationRule, Integer unitByPackaging, Integer refFacPrice) {
        this.stockRegistrationId = stockRegistrationId;
        this.quantity = quantity;
        this.comment = comment;
        this.referenceName = referenceName;
        this.idReference = idReference;
        this.storageAreaName = storageAreaName;
        this.storageAreaId = storageAreaId;
        this.packagingCount = packagingCount;
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.supplierName = supplierName;
        this.unitCount = unitCount;
        this.unitPrice = unitPrice;
        this.calculationRule = calculationRule;
        this.unitByPackaging = unitByPackaging;
        this.refFactPrice = refFacPrice;
    }

    public Long getStockRegistrationId() {
        return stockRegistrationId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public Long getIdReference() {
        return idReference;
    }

    public String getStorageAreaName() {
        return storageAreaName;
    }

    public Long getStorageAreaId() {
        return storageAreaId;
    }

    public Boolean getPackagingCount() {
        return packagingCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getUnitCount() {
        return unitCount;
    }

    public Float getUnitPrice() {
        return unitPrice;
    }

    public String getCalculationRule() {
        return calculationRule;
    }

    public Integer getUnitByPackaging() {
        return unitByPackaging;
    }

    public Integer getRefFactPrice() {
        return refFactPrice;
    }
}
