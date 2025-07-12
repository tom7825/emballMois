package fr.inventory.packaging.service;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.exceptions.entity.EntityAlreadyExist;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.category.CategoryNotFound;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;
import fr.inventory.packaging.repository.ReferenceRepository;
import fr.inventory.packaging.service.api.impl.PackagingReferenceApiServiceImpl;
import fr.inventory.packaging.service.core.CategoryService;
import fr.inventory.packaging.service.core.StockRegistrationService;
import fr.inventory.packaging.service.core.StorageAreaService;
import fr.inventory.packaging.service.validation.ReferenceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackagingReferenceApiServiceTests {

    @InjectMocks
    private PackagingReferenceApiServiceImpl packagingReferenceService;

    @Mock
    private ReferenceRepository referenceRepository;

    @Mock
    private StorageAreaService storageAreaService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ReferenceValidator referenceValidator;

    @Mock
    private StockRegistrationService stockRegistrationService;

    private PackagingReferenceDto packagingReferenceDto;
    private PackagingReference packagingReference;
    private StorageArea storageArea;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        packagingReferenceDto = new PackagingReferenceDto();
        packagingReferenceDto.setReferenceName("TestReference");
        packagingReferenceDto.setAreasName(List.of("Zone1"));
        packagingReferenceDto.setCategoryName("Cat1");
        packagingReferenceDto.setReferenceProductionDB("");
        packagingReferenceDto.setCalculationRule("");
        packagingReferenceDto.setUnitSupplier("");
        packagingReferenceDto.setSupplierName("");
        packagingReferenceDto.setUnitSupplier("m");

        packagingReference = new PackagingReference();
        packagingReference.setReferenceName("TestReference");

        storageArea = new StorageArea();
        storageArea.setStorageAreaName("Zone1");

        category = new Category();
        category.setCategoryName("Cat1");
    }

    @Test
    void testAddReference_whenReferenceDoesNotExist() throws Exception {
        when(referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(anyString())).thenReturn(null);
        when(referenceRepository.save(any(PackagingReference.class))).thenReturn(packagingReference);

        assertDoesNotThrow(() -> packagingReferenceService.addReference(packagingReferenceDto));
        verify(referenceRepository, times(1)).save(any(PackagingReference.class));
    }

    @Test
    void testAddReference_whenParameterIsNull(){
        assertThrows(EntityIllegalParameter.class,()-> packagingReferenceService.addReference(null));
    }

    @Test
    void testAddReference_whenReferenceAlreadyExists() {
        when(referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(anyString())).thenReturn(packagingReference);

        assertThrows(EntityAlreadyExist.class, () -> packagingReferenceService.addReference(packagingReferenceDto));
        verify(referenceRepository, never()).save(any(PackagingReference.class));
    }


    @Test
    void testModifyReferenceReference_whenReferenceExists() throws IncompleteReference, EntityIllegalParameter, CategoryNotFound {
        packagingReference.setIdReference(1L);
        when(referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(anyString())).thenReturn(packagingReference);
        doNothing().when(stockRegistrationService).updateReferenceInStockRegistration(anyString());
        when(categoryService.getActiveCategoryByName(any())).thenReturn(category);
        doNothing().when(referenceValidator).validateReference(any(PackagingReferenceDto.class));
        when(referenceRepository.save(any(PackagingReference.class))).thenReturn(packagingReference);

        assertDoesNotThrow(() -> packagingReferenceService.modifyReference(packagingReferenceDto));
    }

    @Test
    void testModifyReferenceReference_whenReferenceNotFound() throws IncompleteReference, EntityIllegalParameter {
        when(referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(anyString())).thenReturn(null);
        doNothing().when(referenceValidator).validateReference(any(PackagingReferenceDto.class));
        assertThrows(ReferenceNotFound.class, () -> packagingReferenceService.modifyReference(packagingReferenceDto));
    }

    @Test
    void addReference_shouldCallValidateReference_whenDtoIsValid() throws Exception {
        PackagingReferenceDto dto = new PackagingReferenceDto();
        dto.setReferenceName("Test Ref");
        dto.setCategoryName("Category A");
        dto.setUnitCount("unit");
        dto.setUnitPrice(5.5f);
        dto.setCalculationRule("*2");

        when(referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull("Test Ref"))
                .thenReturn(null);

        PackagingReference entity = new PackagingReference();
        when(referenceRepository.save(any())).thenReturn(entity);


        packagingReferenceService.addReference(dto);

        verify(referenceValidator).validateReference(dto);
        verify(referenceRepository).save(any(PackagingReference.class));
    }
}
