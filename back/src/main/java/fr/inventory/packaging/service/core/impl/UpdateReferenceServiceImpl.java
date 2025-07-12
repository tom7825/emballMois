package fr.inventory.packaging.service.core.impl;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.repository.ReferenceRepository;
import fr.inventory.packaging.repository.StorageAreaRepository;
import fr.inventory.packaging.service.core.UpdateReferenceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateReferenceServiceImpl implements UpdateReferenceService {

    private static final Logger logger = LogManager.getLogger();

    private ReferenceRepository referenceRepository;
    private StorageAreaRepository storageAreaRepository;

    public void updateReference(String referenceName, String storageAreaName, Long newId) throws EntityIllegalParameter, ReferenceNotFound {
        logger.info("Update Reference : start");

        if (referenceName == null || referenceName.isEmpty()) {
            logger.error("Update Reference : stop, empty data");
            throw new EntityIllegalParameter("Reference name is empty");
        }

        PackagingReference existingPackingReference =
                referenceRepository.findByReferenceNameAndModificationDateReferenceIsNull(referenceName);

        if (existingPackingReference == null) {
            logger.error("Update Reference : stop, reference not found");
            throw new ReferenceNotFound(referenceName, Error.ENTITY_NOT_FOUND_NAME);
        }

        PackagingReference copy = createPackagingReferenceCopy(existingPackingReference);
        copy.setIdReference(null);
        copy.setModificationDateReference(null);

        StorageArea newArea = storageAreaRepository.findById(newId)
                .orElseThrow(() -> new EntityIllegalParameter("New storage area not found (id=" + newId + ")"));

        List<StorageArea> updatedAreas = copy.getAreas().stream()
                .filter(area -> !area.getStorageAreaName().equals(storageAreaName))
                .collect(Collectors.toList());

        updatedAreas.add(newArea);
        copy.setAreas(updatedAreas);

        existingPackingReference.setModificationDateReference(LocalDateTime.now());
        referenceRepository.save(existingPackingReference);

        copy.setIdMainReference(existingPackingReference.getIdReference());
        referenceRepository.save(copy);

        logger.info("Update Reference : success");
    }

    /**
     * Creates a deep copy of a given {@link PackagingReference}, excluding its ID and modification date.
     * This method is used to prepare a new version of a reference during update operations.
     *
     * @param existingPackingReference the original reference to copy
     * @return a copy of the reference with no ID or modification date set
     */
    private PackagingReference createPackagingReferenceCopy(PackagingReference existingPackingReference) {
        PackagingReference copy = new PackagingReference();
        copy.setReferenceName(existingPackingReference.getReferenceName());
        copy.setUnitSupplier(existingPackingReference.getUnitSupplier());
        copy.setCalculationRule(existingPackingReference.getCalculationRule());
        copy.setUnitPrice(existingPackingReference.getUnitPrice());
        copy.setSupplierName(existingPackingReference.getSupplierName());
        copy.setReferenceProductionDB(existingPackingReference.getReferenceProductionDB());
        copy.setUnitByPackaging(existingPackingReference.getUnitByPackaging());
        copy.setArchivedPackaging(existingPackingReference.isArchivedPackaging());
        copy.setUnitCount(existingPackingReference.getUnitCount());
        copy.setCategory(existingPackingReference.getCategory());
        copy.setAreas(existingPackingReference.getAreas());
        return copy;
    }

    @Autowired
    public void setReferenceRepository(ReferenceRepository referenceRepository) {
        this.referenceRepository = referenceRepository;
    }

    @Autowired
    public void setStorageAreaRepository(StorageAreaRepository storageAreaRepository) {
        this.storageAreaRepository = storageAreaRepository;
    }
}
