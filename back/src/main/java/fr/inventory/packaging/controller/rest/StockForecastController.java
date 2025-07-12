package fr.inventory.packaging.controller.rest;

import fr.inventory.packaging.entity.dto.StockForecastDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.inventory.InsufficientInventoryDataException;
import fr.inventory.packaging.service.forecast.StockForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller responsible for handling stock forecast requests.
 * Provides an endpoint to retrieve estimated remaining days of stock
 * for each reference based on the latest inventory data.
 */
@RestController
@RequestMapping("api/rest/forecast")
public class StockForecastController {

    private StockForecastService stockForecastService;

    /**
     * Retrieves the estimated number of remaining stock days for each reference,
     * based on the two most recent finalized inventories.
     *
     * @return an HTTP response containing the forecast data or an error message
     */
    @GetMapping("/stockdays")
    public ResponseEntity<ApiResponse<?>> getStockForecast() {
        try {
            List<StockForecastDto> forecast =   stockForecastService.computeStockForecast();
            return ResponseEntity.ok(new ApiResponse<>(forecast));
        } catch (InsufficientInventoryDataException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse<>(e.getMessage()));
        }
    }

    @Autowired
    public void setStockForecastService(StockForecastService stockForecastService) {
        this.stockForecastService = stockForecastService;
    }
}
