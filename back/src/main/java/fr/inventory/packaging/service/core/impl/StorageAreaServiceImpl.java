package fr.inventory.packaging.service.core.impl;

import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.storage.area.AreaNotFound;
import fr.inventory.packaging.repository.StorageAreaRepository;
import fr.inventory.packaging.service.core.StorageAreaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static fr.inventory.packaging.service.utils.Utils.checkNameIsValid;

@Service
public class StorageAreaServiceImpl implements StorageAreaService {

    private static final Logger logger = LogManager.getLogger();

    private StorageAreaRepository storageAreaRepository;

    @Override
    public StorageArea getLastModificationAreaByName(String name) throws EntityIllegalParameter, AreaNotFound {
        logger.info("Get area by name : Start");
        checkNameIsValid(name, "zone");
        StorageArea storageArea = storageAreaRepository.findByStorageAreaNameAndModificationDateAreaIsNull(name);
        if (storageArea == null) {
            throw new AreaNotFound(name, Error.ENTITY_NOT_FOUND_NAME);
        }
        logger.info("Get area by name : success");
        return storageArea;
    }

    @Autowired
    public void setAreaRepository(StorageAreaRepository storageAreaRepository) {
        this.storageAreaRepository = storageAreaRepository;
    }
}
