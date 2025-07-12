package fr.inventory.packaging.service.utils;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.dto.PackagingReferenceDto;

public class PackagingReferenceMapper {
    public static PackagingReference fromDto(PackagingReferenceDto ref) {
        return new PackagingReference(
                ref.getReferenceName(),
                ref.getUnitCount(),
                ref.getCalculationRule(),
                ref.getUnitPrice(),
                ref.getSupplierName(),
                ref.getReferenceProductionDB(),
                ref.getUnitByPackaging(),
                ref.isArchivedPackaging(),
                ref.getUnitSupplier(),
                ref.getNumFact()
        );
    }

    public static PackagingReference copyOf(PackagingReference ref) {
        return new PackagingReference(
                ref.getReferenceName(),
                ref.getUnitCount(),
                ref.getCalculationRule(),
                ref.getUnitPrice(),
                ref.getSupplierName(),
                ref.getReferenceProductionDB(),
                ref.getUnitByPackaging(),
                ref.isArchivedPackaging(),
                ref.getAreas(),
                ref.getCategory(),
                ref.getUnitSupplier(),
                ref.getUnitPriceRefFac()
        );
    }
}
