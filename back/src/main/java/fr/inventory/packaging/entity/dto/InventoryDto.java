package fr.inventory.packaging.entity.dto;

import java.time.LocalDateTime;


public class InventoryDto {

    private Long idInventory;

    private LocalDateTime startDateInventory;

    private LocalDateTime endDateInventory;

    public InventoryDto(Long idInventory, LocalDateTime startDateInventory, LocalDateTime endDateInventory) {
        this.idInventory = idInventory;
        this.startDateInventory = startDateInventory;
        this.endDateInventory = endDateInventory;
    }

    public Long getIdInventory() {
        return idInventory;
    }

    public void setIdInventory(Long idInventory) {
        this.idInventory = idInventory;
    }

    public LocalDateTime getStartDateInventory() {
        return startDateInventory;
    }

    public void setStartDateInventory(LocalDateTime startDateInventory) {
        this.startDateInventory = startDateInventory;
    }

    public LocalDateTime getEndDateInventory() {
        return endDateInventory;
    }

    public void setEndDateInventory(LocalDateTime endDateInventory) {
        this.endDateInventory = endDateInventory;
    }
}
