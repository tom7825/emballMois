package fr.inventory.packaging.service.utils;

import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;



public class Utils {

    public static void checkNameIsValid(String name, String objectType) throws EntityIllegalParameter {
        if (name == null || name.isEmpty()) {
            throw new EntityIllegalParameter("Nom de " + objectType + " invalide");
        }
    }
}
