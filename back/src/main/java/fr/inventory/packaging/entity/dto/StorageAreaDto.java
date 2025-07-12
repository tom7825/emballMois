package fr.inventory.packaging.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Data Transfer Object (DTO) for StorageArea.
 * This class is used to transfer storage area data between the backend and the frontend.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StorageAreaDto {

    private Long storageAreaId;

    private String storageAreaName;

    private Boolean storageAreaArchived;

    public StorageAreaDto(Long storageAreaId, String storageAreaName) {
        this.storageAreaId = storageAreaId;
        this.storageAreaName = storageAreaName;
    }

    public StorageAreaDto() {
    }

    public StorageAreaDto(Long storageAreaId, String storageAreaName, Boolean storageAreaArchived) {
        this.storageAreaId = storageAreaId;
        this.storageAreaName = storageAreaName;
        this.storageAreaArchived = storageAreaArchived;
    }

    public String getStorageAreaName() {
        return storageAreaName;
    }

    public void setStorageAreaName(String storageAreaName) { // Correction du setter
        this.storageAreaName = storageAreaName;
    }

    public boolean isStorageAreaArchived() {
        return storageAreaArchived;
    }

    public void setStorageAreaArchived(boolean storageAreaArchived) {
        this.storageAreaArchived = storageAreaArchived;
    }

    public Long getStorageAreaId() {
        return storageAreaId;
    }

    public void setStorageAreaId(Long storageAreaId) {
        this.storageAreaId = storageAreaId;
    }

    public Boolean getStorageAreaArchived() {
        return storageAreaArchived;
    }

    public void setStorageAreaArchived(Boolean storageAreaArchived) {
        this.storageAreaArchived = storageAreaArchived;
    }
}
