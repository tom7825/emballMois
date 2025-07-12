package fr.inventory.packaging.entity;

/**
 * Internal model representing the intermediate calculation data
 * used to estimate remaining stock days for a specific reference.
 * <p>
 * This class is not exposed externally and serves to centralize
 * all values required for forecast computation logic.
 */
public class StockCalculationData {

    /**
     * The unique identifier of the reference.
     */
    private Long referenceId;

    /**
     * The quantity recorded at the previous inventory.
     */
    private double stockBefore;

    /**
     * The quantity recorded at the latest inventory.
     */
    private double stockAfter;

    /**
     * The calculated total consumption between the two inventories.
     */
    private double totalConsumption;

    /**
     * The number of days between the two inventories.
     */
    private int daysBetweenInventories;

    /**
     * The estimated daily consumption.
     */
    private double dailyConsumption;

    /**
     * The stock currently available.
     */
    private double currentStock;

    /**
     * The estimated number of remaining stock days.
     */
    private double remainingDays;

    /**
     * The name of the reference
     */
    private String referenceName;

    /**
     * The name of the category
     */
    private String categoryName;

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public double getStockBefore() {
        return stockBefore;
    }

    public void setStockBefore(double stockBefore) {
        this.stockBefore = stockBefore;
    }

    public double getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(double stockAfter) {
        this.stockAfter = stockAfter;
    }

    public double getTotalConsumption() {
        return totalConsumption;
    }

    public void setTotalConsumption(double totalConsumption) {
        this.totalConsumption = totalConsumption;
    }

    public int getDaysBetweenInventories() {
        return daysBetweenInventories;
    }

    public void setDaysBetweenInventories(int daysBetweenInventories) {
        this.daysBetweenInventories = daysBetweenInventories;
    }

    public double getDailyConsumption() {
        return dailyConsumption;
    }

    public void setDailyConsumption(double dailyConsumption) {
        this.dailyConsumption = dailyConsumption;
    }

    public double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
    }

    public double getRemainingDays() {
        return remainingDays;
    }

    public void setRemainingDays(double remainingDays) {
        this.remainingDays = remainingDays;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
