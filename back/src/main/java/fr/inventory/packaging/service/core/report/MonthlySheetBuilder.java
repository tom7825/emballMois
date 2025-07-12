package fr.inventory.packaging.service.core.report;

import fr.inventory.packaging.entity.StockRegistrationForReport;
import fr.inventory.packaging.exceptions.entity.report.RowCreationException;
import fr.inventory.packaging.exceptions.entity.report.UnableToCreateReport;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Concrete implementation of SheetBuilder for creating a monthly report sheet.
 * This class builds the sheet for the "Rapport mensuel".
 */
public class MonthlySheetBuilder extends SheetBuilder {

    /**
     * Constructor that sets the sheet name to "Rapport mensuel".
     */
    public MonthlySheetBuilder() {
        this.sheetName = "Rapport mensuel";
    }

    @Override
    public void build(XSSFWorkbook workbook) throws UnableToCreateReport {
        try {
            logger.info("Building sheet '{}' in the workbook.", sheetName);

            RowFactory rowFactory = new RowFactory(workbook, areas);
            removeSheetIfExists(workbook);
            Sheet sheet = workbook.createSheet(sheetName);
            logger.info("Sheet '{}' created successfully.", sheetName);
            rowFactory.createHeaderRow(sheet);
            logger.info("Header row created for sheet '{}'.", sheetName);

            int line = 1;
            for (String category : stockData.keySet()) {
                for (StockRegistrationForReport registration : stockData.get(category)) {
                    rowFactory.createStockRow(sheet, line, registration);
                    logger.debug("Added stock row for category '{}', line {}", category, line);
                    line++;
                }
            }
            rowFactory.createFooterRow(sheet,line);
            sheet.createFreezePane(2, 1);
            logger.info("Freeze pane created for sheet '{}'.", sheetName);
            rowFactory.mergeConsecutiveCategoryCells(sheet,line-1);
            logger.info("Merged consecutive category cells for sheet '{}'.", sheetName);


        }catch (RowCreationException e){
            throw new UnableToCreateReport(e.getMessage());
        }
    }

    /**
     * Removes the sheet from the workbook if it already exists.
     *
     * @param workbook the Excel workbook
     */
    private void removeSheetIfExists(XSSFWorkbook workbook) {
        Sheet existing = workbook.getSheet(sheetName);
        logger.info("Checking if sheet '{}' already exists in the workbook.", sheetName);
        if (existing != null) {
            logger.info("Removed existing sheet '{}' from the workbook.", sheetName);
            workbook.removeSheetAt(workbook.getSheetIndex(existing));
        }else{
            logger.info("No existing sheet '{}' found to remove.", sheetName);
        }
    }
}
