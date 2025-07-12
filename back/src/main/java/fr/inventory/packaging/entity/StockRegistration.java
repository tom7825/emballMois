package fr.inventory.packaging.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entity representing a stock registration.
 * It associates a reference, quantity, storage area, and inventory context.
 */
@Entity
@Table(name = "stock_registration")
public class StockRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStockRegistration;

    private LocalDateTime registrationDate;

    private Double quantity;

    @ManyToOne
    @JoinColumn(name = "idArea")
    private StorageArea storageArea;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "idReference")
    private PackagingReference reference;

    @ManyToOne
    @JoinColumn(name = "idInventory")
    @JsonIgnore
    private Inventory inventory;

    private Boolean packagingCount;

    public StockRegistration(Double quantity, String comment, PackagingReference reference, Boolean packagingCount) {
        this.quantity = quantity;
        this.comment = comment;
        this.reference = reference;
        this.packagingCount = packagingCount;
        this.registrationDate = LocalDateTime.now();
    }

    public StockRegistration() {
    }

    public PackagingReference getReference() {
        return reference;
    }

    public void setReference(PackagingReference reference) {
        this.reference = reference;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Long getIdStockRegistration() {
        return idStockRegistration;
    }

    public void setIdStockRegistration(Long idStockRegistration) {
        this.idStockRegistration = idStockRegistration;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
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

    public StorageArea getStorageArea() {
        return storageArea;
    }

    public void setStorageArea(StorageArea storageArea) {
        this.storageArea = storageArea;
    }

    public Boolean getPackagingCount() {
        return packagingCount;
    }

    public void setPackagingCount(Boolean packagingCount) {
        this.packagingCount = packagingCount;
    }
}
