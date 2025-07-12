package fr.inventory.packaging.entity;

import fr.inventory.packaging.entity.dto.StockRegistrationForReportDto;
import fr.inventory.packaging.exceptions.entity.report.WarningStockDataException;
import fr.inventory.packaging.exceptions.entity.report.InvalidStockDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity used for generating Report
 */
public class StockRegistrationForReport {

    private final String categoryName;
    private final String referenceName;
    private final String provider;
    private final Map<String, Double> quantityByArea;
    private final String unity;
    private final Float price;
    private final String calculationRule;
    private String comment;
    private Integer refFacPrice;

    private static final Logger logger = LogManager.getLogger();

    public StockRegistrationForReport(StockRegistrationForReportDto stockRegistrationDto) {
        this.categoryName = stockRegistrationDto.getCategoryName();
        this.referenceName = stockRegistrationDto.getReferenceName();
        this.provider = stockRegistrationDto.getSupplierName();
        this.quantityByArea = new HashMap<>();
        this.unity = stockRegistrationDto.getUnitCount();
        this.price = stockRegistrationDto.getUnitPrice();
        this.calculationRule = stockRegistrationDto.getCalculationRule();
        this.comment = stockRegistrationDto.getComment();
        this.refFacPrice = stockRegistrationDto.getRefFactPrice();
    }

    public String getCategoryName() throws WarningStockDataException {
        if(categoryName == null || categoryName.isEmpty()){
            throw new WarningStockDataException("Catégorie absente pour cette référence");
        }
        return categoryName;
    }

    public String getReferenceName() throws InvalidStockDataException {
        if(referenceName == null || referenceName.isEmpty()){
            logger.error("referenceName is null or empty");
            throw new InvalidStockDataException("Nom de la référence absent");
        }
        return referenceName;
    }

    public String getProvider() throws WarningStockDataException {
        if(provider == null || provider.isEmpty()){
            throw new WarningStockDataException("Fournisseur absent pour cette référence");
        }
        return provider;
    }

    public String getUnity() throws WarningStockDataException {
        if(unity == null || unity.isEmpty()){
            throw new WarningStockDataException("Unité absente pour cette référence");
        }
        return unity;
    }

    public Float getPrice() throws InvalidStockDataException {
        if(price == null || price < 0){
            logger.error("Registration price is invalid");
            throw new InvalidStockDataException("Le prix unitaire est invalide");
        }
        return price;
    }

    public String getCalculationRule() {
        if(calculationRule == null || calculationRule.isEmpty()) {
            return "*1";
        }
        return calculationRule;
    }

    public String getComment() {
        return comment;
    }

    public void addQuantityForArea(Double quantity, String area, Boolean packagedRegistration, Integer quantityByPackaging) {
        if (quantity < 0) {
            if(!comment.isEmpty()) comment += "\r\n";
            comment +=  "La quantité ne peut pas être négative pour la zone de stockage : " + area;
        }
        if(packagedRegistration != null && packagedRegistration){
            if(quantityByPackaging == null || quantityByPackaging <1){
                if(!comment.isEmpty()) comment += "\r\n";
                comment +=  "La quantité par unité de stockage n'est pas valide, impossible d'insérer la valeur de stock dans " + area;
                quantity = 0.0;
            }else {
                quantity = quantity * quantityByPackaging;
            }
        }
        quantityByArea.merge(area, quantity, Double::sum);
    }

    public Double getQuantityFromArea(String area){
        Double quantity = quantityByArea.get(area);
        if(quantity == null){
            return 0.0;
        }
        return quantity;
    }

    public Integer getRefFacPrice() {
        return refFacPrice;
    }
}
