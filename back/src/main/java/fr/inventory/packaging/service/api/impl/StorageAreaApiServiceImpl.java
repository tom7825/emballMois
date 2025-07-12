package fr.inventory.packaging.service.api.impl;

import fr.inventory.packaging.entity.PackagingReference;
import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.entity.dto.StorageAreaDto;
import fr.inventory.packaging.entity.dto.response.ApiResponse;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.reference.ReferenceNotFound;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaAlreadyExists;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;
import fr.inventory.packaging.repository.StorageAreaRepository;
import fr.inventory.packaging.service.core.UpdateReferenceService;
import fr.inventory.packaging.service.api.StorageAreaApiService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageAreaApiServiceImpl implements StorageAreaApiService {

    private static final Logger logger = LogManager.getLogger();

    private StorageAreaRepository storageAreaRepository;

    private UpdateReferenceService updateReferenceService;

    @Override
    public ApiResponse<StorageAreaDto> addArea(StorageAreaDto storageAreaDto) throws EntityIllegalParameter, AreaAlreadyExists {
        logger.info("Add area : start");
        isDtoValid(storageAreaDto);
        try {
            StorageArea storageArea = getLastModifiedAreaByName(storageAreaDto.getStorageAreaName());
            logger.warn("Add area : stop process, area already exists");
            throw new AreaAlreadyExists(storageArea, Error.ENTITY_ALREADY_EXISTS_CREATE);
        } catch (AreaNotFound e) {
            logger.info("Add area : success");
            return new ApiResponse<>(
                    "Nouvelle zone de stockage, " + storageAreaDto.getStorageAreaName() + ", enregistrée.",
                    convertAreaToDto(storageAreaRepository.save(
                            new StorageArea(storageAreaDto.getStorageAreaName())))
            );
        }
    }


    @Override
    public void modifyArea(StorageAreaDto storageAreaDto) throws AreaNotFound, EntityIllegalParameter, AreaAlreadyExists {
        logger.info("Modify area : start");
        isDtoValid(storageAreaDto);
        try{
            StorageArea oldArea = getLastModifiedAreaByName(storageAreaDto.getStorageAreaName());
            StorageArea newArea = new StorageArea(storageAreaDto.getStorageAreaName(), storageAreaDto.isStorageAreaArchived(), oldArea.getStorageAreaId());
            if (!oldArea.equals(newArea)) {
                oldArea.setModificationDateArea(LocalDateTime.now());
                storageAreaRepository.save(oldArea);
                logger.info("Modify area : {} archived.", oldArea.getStorageAreaName());
                Long newId = storageAreaRepository.save(newArea).getStorageAreaId();
                updateAreaOfReference(oldArea.getStorageAreaName(), oldArea.getStorageAreaId(), newId);
                logger.info("Modify area : {} created.", newArea.getStorageAreaName());
            } else {
                logger.info("Modify area : {} is already up to date.", oldArea.getStorageAreaName());
                throw new AreaAlreadyExists(newArea, Error.ENTITY_ALREADY_EXISTS_MODIFY);
            }
        }catch (AreaNotFound e){
            logger.warn("Modify area: stop process, unable to find area {}", storageAreaDto.getStorageAreaName());
            throw new AreaNotFound(storageAreaDto.getStorageAreaName(), Error.ENTITY_NOT_FOUND_NAME);
        }

        logger.info("Modify area : end process, success");

    }

    private void updateAreaOfReference(String storageAreaName, Long oldIdArea, Long newIdArea) throws EntityIllegalParameter {
        List<PackagingReference> references = storageAreaRepository.getRefencesOfAreaByAreaId(oldIdArea);
        for (PackagingReference reference : references) {
            try {
                updateReferenceService.updateReference(reference.getReferenceName(), storageAreaName, newIdArea);
            } catch (ReferenceNotFound e) {
                throw new EntityIllegalParameter("Unable to update Reference " + reference.getReferenceName() + " with area : " + storageAreaName );
            }
        }

    }


    @Override
    public ApiResponse<List<StorageAreaDto>> getActiveAreas() throws AreaNotFound {
        List<StorageArea> areas = storageAreaRepository.findAllByModificationDateAreaIsNullAndStorageAreaArchivedFalse();
        List<StorageAreaDto> areasDto = new ArrayList<>();
        if (areas.isEmpty()) {
            throw new AreaNotFound();
        }
        areas.forEach(area -> areasDto.add(new StorageAreaDto(area.getStorageAreaId(), area.getStorageAreaName())));
        return new ApiResponse<>(areasDto.size() + "zone de stockage trouvée", areasDto);
    }

    @Override
    public ApiResponse<List<StorageAreaDto>> getActiveAreasWithActivesReferences() throws AreaNotFound {
        List<StorageArea> areas = storageAreaRepository.findAllByModificationDateAreaIsNullAndArchivedStorageAreaFalseAndActiveReferences();
        if (areas.isEmpty()) {
            throw new AreaNotFound();
        }
        return new ApiResponse<>(areas.size() + "zone de stockage trouvée",convertAreasToDto(areas));
    }

    @Override
    public ApiResponse<List<StorageAreaDto>> getArchivedAreas() throws AreaNotFound {
        List<StorageArea> areas = storageAreaRepository.findAllByModificationDateAreaIsNullAndStorageAreaArchivedTrue();
        if (areas.isEmpty()) {
            throw new AreaNotFound();
        }
        return new ApiResponse<>(areas.size() + "zone de stockage trouvée",convertAreasToDto(areas));
    }

    /**
     * Retrieves the most recently modified area by its name.
     *
     * @param storageAreaName the name of the area
     * @return the {@link StorageArea} entity
     * @throws AreaNotFound           if no area with the given name is found
     * @throws EntityIllegalParameter if the name is null or invalid
     */
    private StorageArea getLastModifiedAreaByName(String storageAreaName) throws AreaNotFound, EntityIllegalParameter {
        logger.info("Get last modified area by name : Start");
        if (storageAreaName.isEmpty()) {
            logger.warn("Get last modified area by name : Searched name is empty");
            throw new EntityIllegalParameter("Le nom de la zone est vide");
        }
        StorageArea area = storageAreaRepository.findByStorageAreaNameAndModificationDateAreaIsNull(storageAreaName);
        if (area == null) {
            throw new AreaNotFound(storageAreaName, Error.ENTITY_NOT_FOUND);
        }
        logger.info("Get last modified area by name : success");
        return area;
    }

    private List<StorageAreaDto> convertAreasToDto(List<StorageArea> areas) {
        return areas.stream()
                .map(area -> new StorageAreaDto(area.getStorageAreaId(), area.getStorageAreaName(), area.isStorageAreaArchived()))
                .collect(Collectors.toList());
    }

    private StorageAreaDto convertAreaToDto(StorageArea area) {
        return new StorageAreaDto(area.getStorageAreaId(), area.getStorageAreaName(), area.isStorageAreaArchived());
    }

    private void isDtoValid(StorageAreaDto dto) throws EntityIllegalParameter {
        if ((dto) == null) {
            logger.error("Stop process, method parameters is null");
            throw new EntityIllegalParameter("Les données de la zone de stockage sont invalides");
        }
    }

    @Autowired
    public void setAreaRepository(StorageAreaRepository storageAreaRepository) {
        this.storageAreaRepository = storageAreaRepository;
    }

    @Autowired
    public void setUpdateReferenceService(UpdateReferenceService updateReferenceService) {
        this.updateReferenceService = updateReferenceService;
    }
}
