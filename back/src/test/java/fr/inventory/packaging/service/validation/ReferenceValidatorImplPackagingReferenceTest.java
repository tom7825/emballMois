package fr.inventory.packaging.service.validation;

import fr.inventory.packaging.entity.Category;
import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.IncompleteReference;
import fr.inventory.packaging.service.validation.impl.ReferenceValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceValidatorImplPackagingReferenceTest {

    private ReferenceValidator validator;

    @BeforeEach
    void setup() {
        validator = new ReferenceValidatorImpl();
    }

    private PackagingReference createValidReference() {
        PackagingReference ref = new PackagingReference();
        ref.setReferenceName("Ref A");
        ref.setCategory(new Category("Cat A"));
        ref.setUnitCount("Pièce");
        ref.setUnitPrice(10.0f);
        ref.setCalculationRule("*2");
        return ref;
    }

    @Test
    void shouldThrow_whenReferenceIsNull() {
        assertThrows(EntityIllegalParameter.class, () -> validator.validateReference((PackagingReference) null));
    }

    @Test
    void shouldThrow_whenReferenceNameIsNull() {
        PackagingReference ref = createValidReference();
        ref.setReferenceName(null);
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenReferenceNameIsEmpty() {
        PackagingReference ref = createValidReference();
        ref.setReferenceName("");
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenCategoryIsNull() {
        PackagingReference ref = createValidReference();
        ref.setCategory(null);
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenCategoryNameIsEmpty() {
        PackagingReference ref = createValidReference();
        ref.setCategory(new Category(""));
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenUnitCountIsNull() {
        PackagingReference ref = createValidReference();
        ref.setUnitCount(null);
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenUnitCountIsEmpty() {
        PackagingReference ref = createValidReference();
        ref.setUnitCount("");
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenUnitPriceIsNull() {
        PackagingReference ref = createValidReference();
        ref.setUnitPrice(null);
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenUnitPriceIsNegative() {
        PackagingReference ref = createValidReference();
        ref.setUnitPrice(-10f);
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldThrow_whenFormulaIsInvalid() {
        PackagingReference ref = createValidReference();
        ref.setCalculationRule("bad");
        assertThrows(IncompleteReference.class, () -> validator.validateReference(ref));
    }

    @Test
    void shouldNotThrow_whenFormulaIsNull() {
        PackagingReference ref = createValidReference();
        ref.setCalculationRule(null);
        assertDoesNotThrow(() -> validator.validateReference(ref));
    }

    @Test
    void shouldNotThrow_whenFormulaIsEmpty() {
        PackagingReference ref = createValidReference();
        ref.setCalculationRule("");
        assertDoesNotThrow(() -> validator.validateReference(ref));
    }

    @Test
    void shouldNotThrow_whenAllFieldsAreValid() {
        PackagingReference ref = createValidReference();
        assertDoesNotThrow(() -> validator.validateReference(ref));
    }
}
