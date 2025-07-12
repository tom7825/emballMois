package fr.inventory.packaging.entity.dto;

/**
 * Data Transfer Object representing the forecasted stock information
 * for a specific reference.
 * This DTO contains the current stock level, the estimated daily consumption,
 * and the computed number of remaining days based on recent inventory data.
 */
public class StockForecastDto {

    /**
     * The unique name identifying the reference.
     */
    private String referenceName;

    /**
     * The category name of the reference
     */
    private String categoryName;

    /**
     * The current stock level for the reference.
     */
    private double currentStock;

    /**
     * The estimated average daily consumption for the reference.
     */
    private double dailyConsumption;

    /**
     * The number of days the stock is expected to last.
     */
    private double remainingDays;

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
    }

    public double getDailyConsumption() {
        return dailyConsumption;
    }

    public void setDailyConsumption(double dailyConsumption) {
        this.dailyConsumption = dailyConsumption;
    }

    public double getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(double remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
