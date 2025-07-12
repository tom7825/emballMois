package fr.inventory.packaging.service.core.report;

/**
 * Utility class that generates background color indexes for Excel cells,
 * cycling through a predefined range of Excel indexed colors.
 *
 */
public class CategoryColorGenerator {

    private short backColorIntCategory = 26;

    public short getNextColor() {
        if (backColorIntCategory < 63) {
            backColorIntCategory++;
        } else {
            backColorIntCategory = 26;
        }
        return backColorIntCategory;
    }
}
