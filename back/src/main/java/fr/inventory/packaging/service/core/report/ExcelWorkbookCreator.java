package fr.inventory.packaging.service.core.report;

import fr.inventory.packaging.exceptions.entity.report.UnableToCreateReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * Service responsible for creating Excel workbooks and building reports.
 * This class manages the creation of a new Excel workbook and populates it using a provided {@link SheetBuilder}.
 */
public class ExcelWorkbookCreator {

    private final XSSFWorkbook workbook;

    private static final Logger logger = LogManager.getLogger();


    /**
     * Constructor that initializes a new Excel workbook.
     * This constructor logs the creation of the new workbook.
     */
    public ExcelWorkbookCreator() {
        logger.info("New Excel workbook created.");
        this.workbook = new XSSFWorkbook();
    }

    /**
     * Builds an Excel report by using a provided {@link SheetBuilder}.
     * The method delegates the building process to the provided builder and logs the steps of the process.
     *
     * @param sheetBuilder the builder responsible for constructing the report's content in the workbook
     * @return the created Excel workbook populated with the report's data
     * @throws UnableToCreateReport if an error occurs during the report creation process
     */
    public XSSFWorkbook createReport(SheetBuilder sheetBuilder) throws UnableToCreateReport {
        try {
            logger.info("Starting to build the report.");
            sheetBuilder.build(workbook);
            logger.info("Report successfully built.");
            return workbook;
        } catch (Exception e) {
            logger.error("Failed to build the report due to: {}", e.getMessage(), e);
            throw new UnableToCreateReport(e);
        }
    }
}

