package fr.inventory.packaging.exceptions.entity.storage.area;

import fr.inventory.packaging.entity.StorageArea;
import fr.inventory.packaging.exceptions.entity.Error;
import fr.inventory.packaging.exceptions.entity.EntityAlreadyExist;

/**
 * Exception thrown when a storage area already exists in the system.
 * Extends {@link EntityAlreadyExist} to provide additional context about the existing entity.
 */
public class AreaAlreadyExists extends EntityAlreadyExist {

    final StorageArea existingStorageArea;

    public AreaAlreadyExists(StorageArea existingStorageArea, Error error) {
      super(existingStorageArea.getStorageAreaName(), error);
      this.existingStorageArea = existingStorageArea;
    }

  public StorageArea getExistingStorageArea() {
    return existingStorageArea;
  }
}
