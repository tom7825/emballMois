package fr.inventory.packaging.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a unit of measurement.
 * Used for packaging and stock references (e.g. "kg", "L", "pcs").
 */
@Entity
@Table(name = "unit")
public class Unit {

    @Id
    private String unit_value;

    public String getUnit_value() {
        return unit_value;
    }

    public void setUnit_value(String unit_value) {
        this.unit_value = unit_value;
    }
}
