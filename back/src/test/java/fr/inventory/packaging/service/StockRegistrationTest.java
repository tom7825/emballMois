package fr.inventory.packaging.service;

import fr.inventory.packaging.entity.*;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.repository.InventoryRepository;
import fr.inventory.packaging.repository.StockRegistrationRepository;
import fr.inventory.packaging.service.api.impl.StockRegistrationApiServiceImpl;
import fr.inventory.packaging.service.core.PackagingReferenceService;
import fr.inventory.packaging.service.core.StorageAreaService;
import fr.inventory.packaging.service.validation.RegistrationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockRegistrationTest {

    @InjectMocks
    private StockRegistrationApiServiceImpl stockRegistrationApiService;

    @Mock
    private PackagingReferenceService packagingReferenceService;

    @Mock
    private StockRegistrationRepository stockRegistrationRepository;

    @Mock
    private StorageAreaService storageAreaService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private RegistrationValidator registrationValidator;

    private StockRegistrationDto dto;
    private StockRegistration sr;
    private PackagingReference pr;
    private StorageArea sa;
    private Category c;

    @BeforeEach
    void setUp(){
        dto = new StockRegistrationDto();
        dto.setStockRegistrationId(1L);
        dto.setReferenceName("ref123");
        dto.setStorageAreaName("Area1");
        dto.setQuantity(100d);
        dto.setPackagingCount(false);
        dto.setComment("Test comment");

        pr = new PackagingReference();
        pr.setReferenceName("ref123");
        pr.setIdReference(1L);

        sa = new StorageArea();
        sa.setStorageAreaId(1L);
        sa.setStorageAreaName("Area1");

        c = new Category();
        c.setCategoryName("Cat1");

        pr.setCategory(c);
        pr.setAreas(List.of(sa));

        sr = new StockRegistration();
        sr.setReference(pr);
        sr.setStorageArea(sa);


    }

    @Test
    void testUpdateStockRegistration() throws Exception {

        StockRegistration existingRegistration = new StockRegistration();
        existingRegistration.setIdStockRegistration(1L);
        existingRegistration.setReference(new PackagingReference());
        existingRegistration.setStorageArea(new StorageArea());

        when(stockRegistrationRepository.findById(1L)).thenReturn(Optional.of(existingRegistration));
        when(packagingReferenceService.findActiveReferenceByName("ref123")).thenReturn(pr);
        when(stockRegistrationRepository.save(any(StockRegistration.class))).thenReturn(sr);

        stockRegistrationApiService.updateStockRegistration(dto);

        verify(stockRegistrationRepository).save(any(StockRegistration.class));
    }

    @Test
    void testAddStockRegistration() throws Exception {

        when(inventoryRepository.findByEndDateInventoryIsNull()).thenReturn(new Inventory());
        when(packagingReferenceService.findActiveReferenceByName("ref123")).thenReturn(pr);
        when(storageAreaService.getLastModificationAreaByName("Area1")).thenReturn(sa);
        when(stockRegistrationRepository.save(any(StockRegistration.class))).thenReturn(sr);

        ApiResponse<StockRegistrationDto> response = stockRegistrationApiService.addStockRegistration(dto);

        assertNotNull(response);
        assertEquals("Enregistrement de stock pour ref123 sauvegardé dans Area1", response.getMessage());
        verify(stockRegistrationRepository).save(any(StockRegistration.class));
    }
}
