package fr.inventory.packaging.service.core.report;

import fr.inventory.packaging.entity.StockRegistrationForReport;
import fr.inventory.packaging.exceptions.entity.report.UnableToCreateReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

/**
 * Abstract class for building Excel sheets.
 * This class defines the common structure for creating sheets in the workbook.
 */
public abstract class SheetBuilder {

    protected String sheetName;
    protected Map<String, List<StockRegistrationForReport>> stockData;
    protected List<String> areas;

    protected static final Logger logger = LogManager.getLogger();

    /**
     * Abstract method to build the sheet in the given workbook.
     * This method needs to be implemented by subclasses to define how the sheet is constructed.
     *
     * @param workbook the Excel workbook where the sheet will be created
     * @throws UnableToCreateReport if there is an issue while creating the report
     */
    abstract void build(XSSFWorkbook workbook) throws UnableToCreateReport;

    public void setStockData(Map<String, List<StockRegistrationForReport>> stockData) {
        this.stockData = stockData;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }
}
