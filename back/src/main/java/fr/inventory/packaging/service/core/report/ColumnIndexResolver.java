package fr.inventory.packaging.service.core.report;

/**
 * Utility class for resolving column indices in a dynamically built Excel sheet.
 * It computes the correct column positions based on the number of storage areas.
 */
public class ColumnIndexResolver {

    private final int areaCount;

    public ColumnIndexResolver(int areaCount) {
        this.areaCount = areaCount;
    }

    public int getCategoryColumn() {
        return 0;
    }

    public int getReferenceColumn() {
        return 1;
    }

    public int getProviderColumn() {
        return 2;
    }

    public int getFirstStorageAreaColumn() {
        return 3;
    }

    public int getLastStorageAreaColumn() {
        return getFirstStorageAreaColumn() + areaCount - 1;
    }

    public int getStorageAreaColumnIndex(int areaIndex) {
        return getFirstStorageAreaColumn() + areaIndex;
    }

    public int getTotalQuantityColumn() {
        return getLastStorageAreaColumn() + 1;
    }

    public int getUnitColumn() {
        return getTotalQuantityColumn() + 1;
    }

    public int getPriceColumn() {
        return getUnitColumn() + 1;
    }

    public int getTotalPriceColumn() {
        return getPriceColumn() + 1;
    }

    public int getCommentColumn(){
        return getTotalPriceColumn() + 1;
    }

    public int getFixedHeaderColumnIndexAfterArea(int index){
        return index + getLastStorageAreaColumn() + 1;
    }
}
