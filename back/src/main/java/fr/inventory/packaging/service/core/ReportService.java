package fr.inventory.packaging.service.core;

import fr.inventory.packaging.exceptions.entity.report.UnableToCreateReport;

/**
 * Generates Excel reports for inventory stock data using various sheet builder strategies.
 */
public interface ReportService {


    /**
     * Generates a monthly Excel report for the specified inventory.
     *
     * @param idInventory the ID of the inventory
     * @return a byte array representing the Excel report
     * @throws UnableToCreateReport if any issue occurs during data retrieval or report generation
     */
    byte[] createMonthlyReport(Long idInventory) throws UnableToCreateReport;
}
