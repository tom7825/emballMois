package fr.inventory.packaging.service.forecast;


import fr.inventory.packaging.entity.dto.StockForecastDto;
import fr.inventory.packaging.exceptions.inventory.InsufficientInventoryDataException;

import java.util.List;

/**
 * Service interface for computing stock forecast data.
 * <p>
 * Provides methods to retrieve estimated remaining stock days for each reference
 * based on recent inventory data and consumption analysis.
 */
public interface StockForecastService {

    /**
     * Computes the forecasted number of days remaining for each reference.
     *
     * @return a list of stock forecast data transfer objects
     */
    List<StockForecastDto> computeStockForecast() throws InsufficientInventoryDataException;
}