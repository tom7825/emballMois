package fr.inventory.packaging.service.core.report;

import fr.inventory.packaging.entity.StockRegistrationForReport;
import fr.inventory.packaging.entity.dto.StockRegistrationForReportDto;
import fr.inventory.packaging.exceptions.entity.report.InvalidStockDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class responsible for organizing and grouping stock registration data
 * by category for Excel report generation.
 */
public class StockRegistrationOrganizer {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Groups a list of stock registrations by category, merging quantities for the same reference
     * across different storage areas.
     *
     * @param registrations the list of stock registration DTOs to process
     * @param areas         the list of known storage areas; this list will be updated with any new area found
     * @return a map where the key is the category name, and the value is a list of stock entries grouped under that category
     * @throws InvalidStockDataException if the registration list is null
     */
    public static Map<String, List<StockRegistrationForReport>> groupByCategory(List<StockRegistrationForReportDto> registrations, List<String> areas) throws InvalidStockDataException {
        logger.info("Starting grouping of stock registrations by category");
        Map<String, List<StockRegistrationForReport>> finalStockMap = new HashMap<>();

        if (registrations == null) {
            logger.warn("Received null registrations list");
            throw new InvalidStockDataException("Aucune référence pour cet inventaire ");
        }

        for (StockRegistrationForReportDto stockReg : registrations) {
            if (stockReg == null) {
                logger.error("Encountered null StockRegistrationDto in the list");
                continue;
            }

            String area = stockReg.getStorageAreaName();
            if (area != null && !area.trim().isEmpty() && !areas.contains(area)) {
                areas.add(area);
            }

            String category = stockReg.getCategoryName();
            String referenceName = stockReg.getReferenceName();

            logger.debug("Processing stock registration - Category: {}, Reference: {}", category, referenceName);

            List<StockRegistrationForReport> stockList = finalStockMap.computeIfAbsent(category, k -> new ArrayList<>());
            boolean found = false;

            for (StockRegistrationForReport stock : stockList) {
                if (stock.getReferenceName().equals(referenceName)) {
                    stock.addQuantityForArea(stockReg.getQuantity(), stockReg.getStorageAreaName(), stockReg.getPackagingCount(), stockReg.getUnitByPackaging());
                    logger.debug("Updated existing stock with reference: {} in category: {}", referenceName, category);
                    found = true;
                    break;
                }
            }
            if (!found) {
                StockRegistrationForReport newStock = new StockRegistrationForReport(stockReg);
                newStock.addQuantityForArea(stockReg.getQuantity(), stockReg.getStorageAreaName(), stockReg.getPackagingCount(), stockReg.getUnitByPackaging());
                stockList.add(newStock);
                logger.debug("Added new stock entry for reference: {} in category: {}", referenceName, category);
            }
        }
        logger.info("Completed grouping of stock registrations. Total categories: {}", finalStockMap.size());
        return finalStockMap;
    }
}

