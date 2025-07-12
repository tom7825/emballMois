package fr.inventory.packaging.service.core.impl;

import fr.inventory.packaging.entity.StockRegistrationForReport;
import fr.inventory.packaging.exceptions.entity.EntityIllegalParameter;
import fr.inventory.packaging.exceptions.entity.report.InvalidStockDataException;
import fr.inventory.packaging.exceptions.entity.report.UnableToCreateReport;
import fr.inventory.packaging.exceptions.registration.StockRegistrationsNotFound;
import fr.inventory.packaging.service.core.ReportService;
import fr.inventory.packaging.service.core.StockRegistrationService;
import fr.inventory.packaging.service.core.report.ExcelWorkbookCreator;
import fr.inventory.packaging.service.core.report.MonthlySheetBuilder;
import fr.inventory.packaging.service.core.report.SheetBuilder;
import fr.inventory.packaging.service.core.report.StockRegistrationOrganizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ReportServiceImpl implements ReportService {

    private StockRegistrationService stockRegistrationService;

    private static final Logger logger = LogManager.getLogger();

    @Override
    public byte[] createMonthlyReport(Long idInventory) throws UnableToCreateReport {
        logger.info("Request received to generate monthly report for inventory with ID: {}", idInventory);
        SheetBuilder monthlySheetBuilder = new MonthlySheetBuilder();
        return createReport(idInventory, monthlySheetBuilder);
    }

    /**
     * Core method for generating a report using a given SheetBuilder strategy.
     *
     * @param idInventory the inventory ID
     * @param sheetBuilder the sheet builder strategy to use
     * @return the generated report as a byte array
     * @throws UnableToCreateReport if an error occurs during processing
     */
    private byte[] createReport(Long idInventory, SheetBuilder sheetBuilder) throws UnableToCreateReport {

        ExcelWorkbookCreator workbookCreator = new ExcelWorkbookCreator();
        List<String> areas = new ArrayList<>();
        Map<String, List<StockRegistrationForReport>> organizedStockRegistrations;

        try {
            logger.info("Fetching stock registrations for inventory with ID: {}", idInventory);
            organizedStockRegistrations = StockRegistrationOrganizer.groupByCategory(stockRegistrationService.getRegistrationsDtoForReportFromInventory(idInventory), areas);
        } catch (InvalidStockDataException | StockRegistrationsNotFound | EntityIllegalParameter e) {
            logger.error("Error while retrieving data for inventory ID: {}", idInventory, e);
            throw new UnableToCreateReport(e);
        }

        sheetBuilder.setAreas(areas);
        sheetBuilder.setStockData(organizedStockRegistrations);

        try (XSSFWorkbook workbook = workbookCreator.createReport(sheetBuilder);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            logger.info("Generating Excel report for inventory with ID: {}", idInventory);
            workbook.write(byteArrayOutputStream);
            logger.info("Report successfully generated for inventory with ID: {}", idInventory);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("Error during Excel report generation for inventory with ID: {}", idInventory, e);
            throw new UnableToCreateReport("Erreur dans la génération du rapport");
        }
    }

    @Autowired
    public void setStockRegistrationService(StockRegistrationService stockRegistrationService) {
        this.stockRegistrationService = stockRegistrationService;
    }
}
