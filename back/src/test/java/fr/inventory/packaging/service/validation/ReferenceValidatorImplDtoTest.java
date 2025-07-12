package fr.inventory.packaging.service.validation;

import fr.inventory.packaging.entity.dto.PackagingReferenceDto;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;
import fr.inventory.packaging.service.validation.impl.ReferenceValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceValidatorImplDtoTest {

    private ReferenceValidator validator;

    @BeforeEach
    void setup() {
        validator = new ReferenceValidatorImpl();
    }

    private PackagingReferenceDto createValidDto() {
        PackagingReferenceDto dto = new PackagingReferenceDto();
        dto.setReferenceName("Ref 1");
        dto.setCategoryName("Cat A");
        dto.setUnitCount("Unité");
        dto.setUnitPrice(10f);
        dto.setCalculationRule("*2");
        return dto;
    }

    @Test
    void shouldThrowEntityIllegalParameter_whenDtoIsNull() {
        assertThrows(EntityIllegalParameter.class, () -> validator.validateReference((PackagingReferenceDto) null));
    }

    @Test
    void shouldThrowIncompleteReference_whenReferenceNameIsNull() {
        PackagingReferenceDto dto = createValidDto();
        dto.setReferenceName(null);

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenReferenceNameIsEmpty() {
        PackagingReferenceDto dto = createValidDto();
        dto.setReferenceName("");

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenCategoryNameIsNull() {
        PackagingReferenceDto dto = createValidDto();
        dto.setCategoryName(null);

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenCategoryNameIsEmpty() {
        PackagingReferenceDto dto = createValidDto();
        dto.setCategoryName("");

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenUnitCountIsNull() {
        PackagingReferenceDto dto = createValidDto();
        dto.setUnitCount(null);

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenUnitCountIsEmpty() {
        PackagingReferenceDto dto = createValidDto();
        dto.setUnitCount("");

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenUnitPriceIsNull() {
        PackagingReferenceDto dto = createValidDto();
        dto.setUnitPrice(null);

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenUnitPriceIsNegative() {
        PackagingReferenceDto dto = createValidDto();
        dto.setUnitPrice(-1f);

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldThrowIncompleteReference_whenFormulaIsInvalid() {
        PackagingReferenceDto dto = createValidDto();
        dto.setCalculationRule("BAD");

        IncompleteReference ex = assertThrows(IncompleteReference.class, () -> validator.validateReference(dto));
    }

    @Test
    void shouldNotThrow_whenDtoIsValid() {
        PackagingReferenceDto dto = createValidDto();
        assertDoesNotThrow(() -> validator.validateReference(dto));
    }

    @Test
    void shouldNotThrow_whenFormulaIsEmpty() {
        PackagingReferenceDto dto = createValidDto();
        dto.setCalculationRule("");
        assertDoesNotThrow(() -> validator.validateReference(dto));
    }

    @Test
    void shouldNotThrow_whenFormulaIsNull() {
        PackagingReferenceDto dto = createValidDto();
        dto.setCalculationRule(null);
        assertDoesNotThrow(() -> validator.validateReference(dto));
    }
}
