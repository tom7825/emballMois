package fr.inventory.packaging.service.forecast.impl;


import fr.inventory.packaging.entity.Inventory;
import fr.inventory.packaging.entity.StockCalculationData;
import fr.inventory.packaging.entity.dto.StockForecastDto;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.exceptions.inventory.InsufficientInventoryDataException;
import fr.inventory.packaging.repository.InventoryRepository;
import fr.inventory.packaging.repository.StockRegistrationRepository;
import fr.inventory.packaging.service.forecast.StockForecastService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link StockForecastService}.
 * <p>
 * This class is responsible for orchestrating the computation of stock forecasts
 * by interacting with inventory data and consumption logic.
 */
@Service
public class StockForecastServiceImpl implements StockForecastService {

    private static final Logger logger = LogManager.getLogger();

    private InventoryRepository inventoryRepository;
    private StockRegistrationRepository stockRegistrationRepository;

    /**
     * Computes the forecasted number of days remaining for each reference.
     *
     * @return a list of stock forecast data transfer objects
     */
    @Override
    public List<StockForecastDto> computeStockForecast() throws InsufficientInventoryDataException {
        logger.info("Starting stock forecast computation.");

        List<Inventory> inventories = getTwoLastInventories();
        Inventory previousInventory = inventories.get(1);
        Inventory latestInventory = inventories.get(0);

        logger.info("Previous inventory ID: {}, date: {}", previousInventory.getIdInventory(), previousInventory.getEndDateInventory());
        logger.info("Latest inventory ID: {}, date: {}", latestInventory.getIdInventory(), latestInventory.getEndDateInventory());

        List<StockRegistrationDto> previousDtos = getStockRegistrationDtos(previousInventory.getIdInventory());
        List<StockRegistrationDto> latestDtos = getStockRegistrationDtos(latestInventory.getIdInventory());


        Map<Long, String> referenceNames = new HashMap<>();
        Map<Long, String> categoryNames = new HashMap<>();

        Stream.concat(previousDtos.stream(), latestDtos.stream())
                .forEach(dto -> {
                    referenceNames.putIfAbsent(dto.getIdReference(), dto.getReferenceName());
                    categoryNames.putIfAbsent(dto.getIdReference(), dto.getCategoryName());
                });

        logger.info("Loaded {} stock registrations from previous inventory.", previousDtos.size());
        logger.info("Loaded {} stock registrations from latest inventory.", latestDtos.size());

        Map<Long, Double> previousStockMap = computeTotalQuantityPerReference(previousDtos);
        Map<Long, Double> latestStockMap = computeTotalQuantityPerReference(latestDtos);

        int daysBetween = (int) ChronoUnit.DAYS.between(
                previousInventory.getEndDateInventory(),
                latestInventory.getEndDateInventory()
        );

        logger.info("Days between inventories: {}", daysBetween);

        if (daysBetween == 0) {
            logger.warn("Inventories are on the same day. Cannot compute forecast.");
            throw new InsufficientInventoryDataException(
                    "Impossible de calculer une prévision : les deux inventaires doivent être espacés d'au moins un jour."
            );
        }

        Map<Long, StockCalculationData> calculationMap = buildCalculationData(
                previousStockMap,
                latestStockMap,
                daysBetween
        );

        for (Map.Entry<Long, StockCalculationData> entry : calculationMap.entrySet()) {
            String referenceName = Objects.requireNonNullElse(referenceNames.get(entry.getKey()), "Référence inconnue");
            String categoryName = Objects.requireNonNullElse(categoryNames.get(entry.getKey()), "Catégorie inconnue");
            entry.getValue().setReferenceName(referenceName);
            entry.getValue().setCategoryName(categoryName);
        }

        logger.info("Built forecast calculation data for {} references.", calculationMap.size());

        return buildForecastDtos(calculationMap);
    }

    /**
     * Retrieves the two most recently finalized inventories,
     * ordered by descending end date.
     *
     * @return a list containing the two latest inventories
     */
    private List<Inventory> getTwoLastInventories() throws InsufficientInventoryDataException {
        List<Inventory> inventories = inventoryRepository
                .findTop2ByEndDateInventoryNotNullOrderByEndDateInventoryDesc();

        if (inventories.size() < 2) {
            logger.warn("Not enough finalized inventories found ({}).", inventories.size());
            throw new InsufficientInventoryDataException(
                    "Impossible de calculer une prévision : au moins deux inventaires finalisés sont requis."
            );
        }

        logger.info("Successfully retrieved the two most recent finalized inventories.");
        return inventories;
    }

    /**
     * Retrieves all stock registration DTOs associated with a given inventory ID.
     *
     * @param inventoryId the ID of the inventory
     * @return a list of stock registration DTOs
     */
    private List<StockRegistrationDto> getStockRegistrationDtos(Long inventoryId) {
        logger.debug("Fetching stock registrations for inventory ID: {}", inventoryId);
        return stockRegistrationRepository.findDtoByInventory_IdInventory(inventoryId);
    }

    /**
     * Aggregates total quantities per reference from a list of stock registrations.
     *
     * @param registrations list of stock registration DTOs
     * @return a map of reference ID to total quantity
     */
    private Map<Long, Double> computeTotalQuantityPerReference(List<StockRegistrationDto> registrations) {
        logger.debug("Computing total quantity per reference for {} stock registrations.", registrations.size());
        return registrations.stream()
                .collect(
                        Collectors.groupingBy(
                                StockRegistrationDto::getIdReference,
                                Collectors.summingDouble(
                                        dto -> {
                                            double qty = dto.getQuantity() != null ? dto.getQuantity() : 0.0;
                                            if (Boolean.TRUE.equals(dto.getPackagingCount()) && dto.getUnitByPackaging() != null) {
                                                qty *= dto.getUnitByPackaging();
                                            }
                                            return qty;
                                        }
                                )
                        )
                );
    }

    /**
     * Builds calculation data objects per reference using stock snapshots from both inventories.
     *
     * @param previousInventoryData  stock quantities from the earlier inventory
     * @param latestInventoryData    stock quantities from the most recent inventory
     * @param daysBetweenInventories number of days between the two inventories
     * @return a map of reference ID to calculation data
     */
    private Map<Long, StockCalculationData> buildCalculationData(
            Map<Long, Double> previousInventoryData,
            Map<Long, Double> latestInventoryData,
            int daysBetweenInventories) {

        logger.debug("Building calculation data from stock maps ({} previous, {} latest).",
                previousInventoryData.size(), latestInventoryData.size());

        Map<Long, StockCalculationData> result = new HashMap<>();

        Set<Long> referenceIds = new HashSet<>();
        referenceIds.addAll(previousInventoryData.keySet());
        referenceIds.addAll(latestInventoryData.keySet());

        for (Long referenceId : referenceIds) {
            double stockBefore = previousInventoryData.getOrDefault(referenceId, 0.0);
            double stockAfter = latestInventoryData.getOrDefault(referenceId, 0.0);

            double totalConsumption = stockBefore - stockAfter;
            double dailyConsumption = totalConsumption / daysBetweenInventories;

            double currentStock = stockAfter;
            double remainingDays = (dailyConsumption > 0) ? currentStock / dailyConsumption : Double.POSITIVE_INFINITY;

            StockCalculationData data = new StockCalculationData();
            data.setReferenceId(referenceId);
            data.setStockBefore(stockBefore);
            data.setStockAfter(stockAfter);
            data.setTotalConsumption(totalConsumption);
            data.setDaysBetweenInventories(daysBetweenInventories);
            data.setDailyConsumption(dailyConsumption);
            data.setCurrentStock(currentStock);
            data.setRemainingDays(remainingDays);

            result.put(referenceId, data);
        }

        return result;
    }

    /**
     * Converts the internal calculation data into forecast DTOs for the frontend.
     *
     * @param calculationDataMap map of reference ID to calculation data
     * @return a list of forecast DTOs
     */
    private List<StockForecastDto> buildForecastDtos(Map<Long, StockCalculationData> calculationDataMap) {
        logger.debug("Transforming calculation data into forecast DTOs for {} references.", calculationDataMap.size());
        return calculationDataMap.values().stream()
                .map(data -> {
                    StockForecastDto dto = new StockForecastDto();
                    dto.setReferenceName(data.getReferenceName());
                    dto.setCategoryName(data.getCategoryName());
                    dto.setCurrentStock(data.getCurrentStock());
                    dto.setDailyConsumption(data.getDailyConsumption());
                    dto.setRemainingDays(data.getRemainingDays());
                    return dto;
                })
                .toList();
    }

    @Autowired
    public void setInventoryRepository(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Autowired
    public void setStockRegistrationRepository(StockRegistrationRepository stockRegistrationRepository) {
        this.stockRegistrationRepository = stockRegistrationRepository;
    }
}
