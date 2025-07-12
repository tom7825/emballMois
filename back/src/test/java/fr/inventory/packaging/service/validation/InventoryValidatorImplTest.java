package fr.inventory.packaging.service.validation;

import fr.inventory.packaging.entity.dto.validation.LineValidationDto;
import fr.inventory.packaging.entity.dto.validation.RegistrationValidationDto;
import fr.inventory.packaging.entity.dto.StockRegistrationDto;
import fr.inventory.packaging.entity.dto.AreaReferenceDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.service.core.PackagingReferenceService;
import fr.inventory.packaging.service.core.StockRegistrationService;
import fr.inventory.packaging.service.validation.impl.InventoryValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryValidatorImplTest {

    @InjectMocks
    private InventoryValidatorImpl inventoryValidator;

    @Mock
    private StockRegistrationService stockRegistrationService;

    @Mock
    private PackagingReferenceService packagingReferenceService;

    @Mock
    private RegistrationValidator registrationValidator;

    @Mock
    private ReferenceValidator referenceValidator;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateInventory_successful() throws Exception {
        Long inventoryId = 1L;

        StockRegistrationDto regDto = mock(StockRegistrationDto.class);
        when(regDto.getStorageAreaName()).thenReturn("Area1");
        when(regDto.getStorageAreaId()).thenReturn(100L);
        when(regDto.getIdReference()).thenReturn(10L);
        when(regDto.getReferenceName()).thenReturn("reg");

        List<StockRegistrationDto> stockRegistrationDtos = Collections.singletonList(regDto);
        when(stockRegistrationService.getRegistrationsDtoFromInventory(inventoryId)).thenReturn(stockRegistrationDtos);

        LineValidationDto lineValidationDto = new LineValidationDto();
        lineValidationDto.setReferenceId(10L);
        AreaReferenceDto areaReferenceDto = new AreaReferenceDto("Area1",lineValidationDto);
        when(packagingReferenceService.findAllActiveReferencesNameByAreaName()).thenReturn(Collections.singletonList(areaReferenceDto));

        LineValidationDto foundLineValidationDto = new LineValidationDto();
        when(packagingReferenceService.findReferenceValidationById(10L)).thenReturn(foundLineValidationDto);

        doNothing().when(referenceValidator).validateReferenceForInventoryValidation(any(LineValidationDto.class), anyBoolean());
        when(registrationValidator.validateRegistrationForInventoryValidation(any(RegistrationValidationDto.class))).thenReturn(Collections.emptyMap());

        ApiResponse<Map<String, List<LineValidationDto>>> response = inventoryValidator.validateInventory(inventoryId);

        assertNotNull(response);
        Map<String, List<LineValidationDto>> validationStructure = response.getData();
        assertTrue(validationStructure.containsKey("Area1"));
        assertFalse(validationStructure.get("Area1").isEmpty());

        verify(stockRegistrationService).getRegistrationsDtoFromInventory(inventoryId);
        verify(packagingReferenceService).findAllActiveReferencesNameByAreaName();
        verify(referenceValidator).validateReferenceForInventoryValidation(any(LineValidationDto.class), anyBoolean());
        verify(registrationValidator).validateRegistrationForInventoryValidation(any(RegistrationValidationDto.class));
    }

    @Test
    void testValidateInventory_emptyStockRegistrations() throws Exception {
        Long inventoryId = 2L;

        when(stockRegistrationService.getRegistrationsDtoFromInventory(inventoryId)).thenReturn(Collections.emptyList());

        when(packagingReferenceService.findAllActiveReferencesNameByAreaName()).thenReturn(Collections.emptyList());

        ApiResponse<Map<String, List<LineValidationDto>>> response = inventoryValidator.validateInventory(inventoryId);

        assertNotNull(response);
        assertTrue(response.getData().isEmpty());

        verify(stockRegistrationService).getRegistrationsDtoFromInventory(inventoryId);
        verify(packagingReferenceService).findAllActiveReferencesNameByAreaName();
        verifyNoInteractions(registrationValidator, referenceValidator);
    }

    @Test
    void testValidateInventory_nullInventoryId_throwsException() {
        assertThrows(EntityIllegalParameter.class, () -> {
            inventoryValidator.validateInventory(null);
        });
    }

    @Test
    void testValidateInventory_stockRegistrationServiceThrows() throws Exception {
        Long inventoryId = 3L;

        when(stockRegistrationService.getRegistrationsDtoFromInventory(inventoryId))
                .thenThrow(new StockRegistrationsNotFound(1L));

        assertThrows(StockRegistrationsNotFound.class, () -> {
            inventoryValidator.validateInventory(inventoryId);
        });

        verify(stockRegistrationService).getRegistrationsDtoFromInventory(inventoryId);
        verify(packagingReferenceService).findAllActiveReferencesNameByAreaName();
        verifyNoMoreInteractions(registrationValidator, referenceValidator);
    }
}
