package fr.inventory.packaging.exceptions.entity.dbproduction;

import fr.inventory.packaging.exceptions.entity.EntityNotFound;

public class DataNotFoundInDbProduction extends EntityNotFound {
    public DataNotFoundInDbProduction(String message) {
        super(message);
    }
}
