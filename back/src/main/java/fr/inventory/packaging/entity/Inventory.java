package fr.inventory.packaging.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an inventory session in the system.
 * This entity stores information about the inventory's start and end dates,
 * as well as a list of stock registrations associated with it.
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventory;

    private final LocalDateTime startDateInventory;

    private LocalDateTime endDateInventory;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private final List<StockRegistration> registrations = new ArrayList<>();

    public Inventory() {
        this.startDateInventory = LocalDateTime.now();
    }

    public void closeInventory() {
        this.endDateInventory = LocalDateTime.now();
    }

    public void addRegistration(StockRegistration stockRegistration) {
        this.registrations.add(stockRegistration);
    }

    public List<StockRegistration> getRegistrations() {
        return registrations;
    }

    public Long getIdInventory() {
        return idInventory;
    }

    public LocalDateTime getStartDateInventory() {
        return startDateInventory;
    }

    public LocalDateTime getEndDateInventory() {
        return endDateInventory;
    }


}
