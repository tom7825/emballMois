package fr.inventory.packaging.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StorageArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for PackagingReference.
 * This class is used to transfer packaging reference data between the backend and the frontend.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PackagingReferenceDto {

    private Long idReference;

    private String referenceName;

    private String unitSupplier;

    private String unitCount;

    private String calculationRule;

    private Float unitPrice;

    private String supplierName;

    private String referenceProductionDB;

    private Integer unitByPackaging;

    private Boolean archivedPackaging;

    private String categoryName;

    private List<String> areasName = new ArrayList<>();

    private Integer numFact;

    public PackagingReferenceDto(Long idReference, String referenceName, String unitCount) {
        this.idReference = idReference;
        this.referenceName = referenceName;
        this.unitCount = unitCount;
    }

    public PackagingReferenceDto() {
    }

    public PackagingReferenceDto(PackagingReference reference) {
        idReference = reference.getIdReference();
        referenceName = reference.getReferenceName();
        unitSupplier = reference.getUnitSupplier();
        unitCount = reference.getUnitCount();
        calculationRule = reference.getCalculationRule();
        unitPrice = reference.getUnitPrice();
        supplierName = reference.getSupplierName();
        unitByPackaging = reference.getUnitByPackaging();
        archivedPackaging = reference.isArchivedPackaging();
        numFact = reference.getUnitPriceRefFac();
        referenceProductionDB = reference.getReferenceProductionDB();
        if (reference.getCategory() != null) {
            categoryName = reference.getCategory().getCategoryName();
        }
        if (!reference.getAreas().isEmpty()) {
            for (StorageArea area : reference.getAreas()) {
                areasName.add(area.getStorageAreaName());
            }
        }

    }

    public PackagingReferenceDto(Long idReference, String referenceName, String unitSupplier, String unitCount, String calculationRule, Float unitPrice, String supplierName, Integer unitByPackaging, String categoryName, Integer numFact) {
        this.idReference = idReference;
        this.referenceName = referenceName;
        this.unitSupplier = unitSupplier;
        this.unitCount = unitCount;
        this.calculationRule = calculationRule;
        this.unitPrice = unitPrice;
        this.supplierName = supplierName;
        this.unitByPackaging = unitByPackaging;
        this.categoryName = categoryName;
        this.numFact = numFact;
    }

    public PackagingReferenceDto(Long idReference, String referenceName, String unitCount, Integer unitByPackaging) {
        this.idReference = idReference;
        this.referenceName = referenceName;
        this.unitCount = unitCount;
        this.unitByPackaging = unitByPackaging;
    }

    // Getters and Setters

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getUnitSupplier() {
        return unitSupplier;
    }

    public void setUnitSupplier(String unitSupplier) {
        this.unitSupplier = unitSupplier;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getReferenceProductionDB() {
        return referenceProductionDB;
    }

    public void setReferenceProductionDB(String referenceProductionDB) {
        this.referenceProductionDB = referenceProductionDB;
    }

    public Integer getUnitByPackaging() {
        return unitByPackaging;
    }

    public void setUnitByPackaging(Integer unitByPackaging) {
        this.unitByPackaging = unitByPackaging;
    }

    public Boolean isArchivedPackaging() {
        return archivedPackaging;
    }

    public void setArchivedPackaging(Boolean archivedPackaging) {
        this.archivedPackaging = archivedPackaging;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getAreasName() {
        return areasName;
    }

    public void setAreasName(List<String> areasName) {
        this.areasName = areasName;
    }

    public Long getIdReference() {
        return idReference;
    }

    public void setIdReference(Long idReference) {
        this.idReference = idReference;
    }

    public String getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(String unitCount) {
        this.unitCount = unitCount;
    }

    public Boolean getArchivedPackaging() {
        return archivedPackaging;
    }

    public Integer getNumFact() {
        return numFact;
    }

    public void setNumFact(Integer numFact) {
        this.numFact = numFact;
    }
}
