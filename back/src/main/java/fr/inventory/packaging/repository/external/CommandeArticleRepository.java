package fr.inventory.packaging.repository.external;

import fr.inventory.packaging.entity.external.CommandeArticle;
import fr.inventory.packaging.entity.external.dto.LastInformationProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommandeArticleRepository extends JpaRepository<CommandeArticle,Double> {

    /**
     * Retrieves a list of unit prices and invoice numbers from supplier invoices,
     * filtered by article code and creation date, excluding invoices with a null delivery reference.
     * The results are ordered by delivery date in descending order.
     *
     * @param referenceName the article code to filter by (COD_ART)
     * @param lastUpdate    only invoices created after this date (DAT_CRE) will be included
     * @return an optional list of projected results containing unit price and invoice number
     */
    @Query(value = """
        SELECT
            lcf.PV_UNI AS prixUnitaire,
            cf.NUM_PIECE as numPiece,
            p.LIB_PRO as libPro
        FROM GC_FACTUREFOUR cf
        LEFT JOIN GC_LIGNEFACTUREFOUR lcf ON cf.NUM_PIECE = lcf.NUM_PIECE
        LEFT JOIN PRODUCTEUR p on cf.COD_FOUR = p.COD_PRO
        WHERE cf.DAT_CRE > :lastUpdate
        AND lcf.LIB_ART = :referenceName
        AND cf.NUM_PIECEBR is not null
        ORDER BY cf.DAT_LIV DESC
        """, nativeQuery = true)
    Optional<List<LastInformationProjection>> getLastInformationsBasedOnLastDateLiv(
            @Param("referenceName")String referenceName,
            @Param("lastUpdate") LocalDateTime lastUpdate);

    @Query(value = """
    SELECT 1
    FROM ARTICLE
    WHERE LIB_ART = :referenceName
    ROWS 1
    """, nativeQuery = true)
    Integer isArticleExistsRaw(@Param("referenceName") String referenceName);


}
