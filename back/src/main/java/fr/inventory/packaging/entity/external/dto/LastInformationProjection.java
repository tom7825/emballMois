package fr.inventory.packaging.entity.external.dto;

/**
 * Projection interface used to retrieve partial data from supplier invoice queries.
 * Specifically extracts the unit price and invoice number from joined invoice and line tables.
 *
 * This interface is typically used with native queries to avoid loading full entity objects.
 */
public interface LastInformationProjection {

    /**
     * Gets the unit price (PV_UNI) of the article.
     *
     * @return the unit price, may be null
     */
    Double getPrixUnitaire();

    /**
     * Gets the invoice number (NUM_PIECE) associated with the unit price.
     *
     * @return the invoice number, may be null
     */
    Double getNumPiece();

    /**
     * Gets supplier's name for the price and invoice
     *
     * @return the supplier's name, may be null
     */
    String getLibPro();
}