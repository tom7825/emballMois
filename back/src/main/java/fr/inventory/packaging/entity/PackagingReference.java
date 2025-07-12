package fr.inventory.packaging.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a packaging reference in the inventory system.
 * This entity stores information about the packaging unit, its supplier, pricing, and its categorization.
 */
@Entity
@Table(name = "packaging_reference")
public class PackagingReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private LocalDateTime modificationDateReference;

    private Long idMainReference;

    private Integer unitPriceRefFac;

    @ManyToOne
    @JoinColumn(name = "idCategory")
    private Category category;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "areas_references",
            joinColumns = @JoinColumn(name = "idReference"),
            inverseJoinColumns = @JoinColumn(name = "idArea")
    )
    private List<StorageArea> areas = new ArrayList<>();

    public PackagingReference(String referenceName, String unitCount, String calculationRule, Float unitPrice,
                              String supplierName, String referenceProductionDB, Integer unitByPackaging,
                              Boolean archivedPackaging,
                              String unitSupplier, Integer unitPriceRefFac) {
        this.referenceName = referenceName;
        this.unitSupplier = unitSupplier;
        this.calculationRule = calculationRule;
        this.unitPrice = unitPrice;
        this.supplierName = supplierName;
        this.referenceProductionDB = referenceProductionDB;
        this.unitByPackaging = unitByPackaging;
        this.archivedPackaging = archivedPackaging;
        this.unitCount = unitCount;
        this.unitPriceRefFac = unitPriceRefFac;
    }


    public PackagingReference(){}

    public PackagingReference(String referenceName, String unitCount, String calculationRule, Float unitPrice,
                              String supplierName, String referenceProductionDB, Integer unitByPackaging,
                              Boolean archivedPackaging, List<StorageArea> areas, Category category,
                              String unitSupplier, Integer unitPriceRefFac) {
        this.referenceName = referenceName;
        this.unitCount = unitCount;
        this.calculationRule = calculationRule;
        this.unitPrice = unitPrice;
        this.supplierName = supplierName;
        this.referenceProductionDB = referenceProductionDB;
        this.unitByPackaging = unitByPackaging;
        this.archivedPackaging = archivedPackaging;
        this.areas.addAll(areas);
        this.category = category;
        this.unitSupplier = unitSupplier;
        this.unitPriceRefFac = unitPriceRefFac;
    }

    /**
     * Adds a storage area to the list of areas where this reference is used.
     *
     * @param storageArea the storage area to add
     */
    public void addArea(StorageArea storageArea) {
        if(!areas.contains(storageArea)) {
            areas.add(storageArea);
        }
    }

    /**
     * Determines whether this PackagingReference is equal to another object.
     * Two PackagingReferences are considered equal if they have the same reference name,
     * the same archived status, the same unit, the same calculation rule, the same unit price,
     * the same supplier name, and the same number of units per packaging.
     *
     * @param obj the object to compare with this PackagingReference
     * @return true if the objects represent the same PackagingReference, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PackagingReference packagingReference = (PackagingReference) obj;
        boolean categoryEquals = false;
        if(category != null){
            categoryEquals = category.equals(packagingReference.category);
        }else if (packagingReference.category == null){
            categoryEquals = true;
        }
        return archivedPackaging == packagingReference.archivedPackaging &&
                Objects.equals(referenceName, packagingReference.referenceName) &&
                Objects.equals(unitSupplier, packagingReference.unitSupplier) &&
                Objects.equals(unitCount, packagingReference.unitCount) &&
                Objects.equals(calculationRule, packagingReference.calculationRule) &&
                Objects.equals(unitPrice, packagingReference.unitPrice) &&
                Objects.equals(supplierName, packagingReference.supplierName) &&
                Objects.equals(unitByPackaging, packagingReference.unitByPackaging) &&
                Objects.equals(referenceProductionDB, packagingReference.referenceProductionDB) &&
                categoryEquals;
    }

    // Getters and Setters

    public Long getIdReference() {
        return idReference;
    }

    public void setIdReference(Long idReference) {
        this.idReference = idReference;
    }

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

    public LocalDateTime getModificationDateReference() {
        return modificationDateReference;
    }

    public void setModificationDateReference(LocalDateTime modificationDateReference) {
        this.modificationDateReference = modificationDateReference;
    }

    public Long getIdMainReference() {
        return idMainReference;
    }

    public void setIdMainReference(Long idMainReference) {
        this.idMainReference = idMainReference;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<StorageArea> getAreas() {
        return areas;
    }

    public void setAreas(List<StorageArea> areas) {
        this.areas = areas;
    }

    public String getUnitCount() {
        return unitCount;
    }

    public void setUnitCount(String unitCount) {
        this.unitCount = unitCount;
    }

    public Integer getUnitPriceRefFac() {
        return unitPriceRefFac;
    }

    public void setUnitPriceRefFac(Integer unitPriceRefFac) {
        this.unitPriceRefFac = unitPriceRefFac;
    }
}
