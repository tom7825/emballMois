package fr.inventory.packaging.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a storage area where inventory items are stored.
 * This entity contains information about the storage location, including its name, archival status,
 * modification date, and parent storage area.
 */
@Entity
@Table(name = "storage_area")
public class StorageArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storageAreaId;

    private String storageAreaName;

    private Boolean storageAreaArchived;

    private LocalDateTime modificationDateArea;

    private Long idMainArea;

    public StorageArea(String storageAreaName, Boolean archivedStorageArea, Long idMainArea) {
        this.storageAreaName = storageAreaName;
        this.storageAreaArchived = archivedStorageArea;
        this.idMainArea = idMainArea;
    }

    public StorageArea(String storageAreaName) {
        this.storageAreaName = storageAreaName;
        this.storageAreaArchived = false;
    }

    public StorageArea(){}

    @Override
    public int hashCode() {
        return Objects.hash(storageAreaName, storageAreaArchived);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StorageArea storageArea = (StorageArea) obj;
        return storageAreaArchived == storageArea.storageAreaArchived &&
                Objects.equals(storageAreaName, storageArea.storageAreaName);
    }

    // Getters and Setters

    public Long getStorageAreaId() {
        return storageAreaId;
    }

    public void setStorageAreaId(Long storageAreaId) {
        this.storageAreaId = storageAreaId;
    }

    public String getStorageAreaName() {
        return storageAreaName;
    }

    public void setStorageAreaName(String storageAreaName) {
        this.storageAreaName = storageAreaName;
    }

    public Boolean isStorageAreaArchived() {
        return storageAreaArchived;
    }

    public void setStorageAreaArchived(Boolean storageAreaArchived) {
        this.storageAreaArchived = storageAreaArchived;
    }

    public LocalDateTime getModificationDateArea() {
        return modificationDateArea;
    }

    public void setModificationDateArea(LocalDateTime modificationDateArea) {
        this.modificationDateArea = modificationDateArea;
    }

    public Long getIdMainArea() {
        return idMainArea;
    }

    public void setIdMainArea(Long idMainArea) {
        this.idMainArea = idMainArea;
    }
}
