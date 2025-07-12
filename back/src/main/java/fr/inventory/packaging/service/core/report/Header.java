package fr.inventory.packaging.service.core.report;

/**
 * Enum representing the different headers used in the Excel report.
 * Each constant is associated with a label that will be displayed in the report.
 */
public enum Header {

    CATEGORY("Catégorie"),
    REFERENCE("Référence"),
    PROVIDER("Fournisseur"),
    TOTAL_QUANTITY("Qté totale"),
    UNIT("Unité"),
    PRICE("Prix unitaire"),
    TOTAL("Total"),
    COMMENT("COMMENTAIRE");

    private final String label;

    Header(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
