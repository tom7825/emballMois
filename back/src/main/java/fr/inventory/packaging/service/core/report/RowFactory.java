package fr.inventory.packaging.service.core.report;

import fr.inventory.packaging.entity.StockRegistrationForReport;

import fr.inventory.packaging.exceptions.entity.report.RowCreationException;
import fr.inventory.packaging.exceptions.entity.report.WarningStockDataException;
import fr.inventory.packaging.exceptions.entity.report.InvalidStockDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.math.BigDecimal;
import java.util.List;

/**
 * Utility class responsible for creating and formatting rows in an Excel sheet for inventory reporting.
 */
public class RowFactory {
    private final List<String> areas;

    private final CellStyleFactory cellStyleFactory;
    private final ColumnIndexResolver indexResolver;

    private static final Logger logger = LogManager.getLogger();

    /**
     * Constructs a new RowFactory for a given workbook and list of storage areas.
     *
     * @param workbook the Excel workbook
     * @param areas the list of storage area names
     */
    public RowFactory(XSSFWorkbook workbook, List<String> areas) {
        this.areas = areas;
        this.cellStyleFactory = new CellStyleFactory(workbook);
        this.indexResolver = new ColumnIndexResolver(areas.size());
        logger.info("RowFactory initialized with {} storage areas.", areas.size());
    }


    /**
     * Creates the header row in the given sheet, including fixed headers, dynamic area headers,
     * and adjusts column widths.
     *
     * @param sheet the Excel sheet where the header row will be created
     * @throws RowCreationException if an error occurs while creating the row
     */
    public void createHeaderRow(Sheet sheet) throws RowCreationException {
        Row row = sheet.createRow(0);
        CellStyle headerStyle = cellStyleFactory.getHeaderCellStyle();
        logger.info("Creating header row for sheet: {}", sheet.getSheetName());
        try {
            for (int i = 0; i < 3; i++) {
                createCell(row, i, Header.values()[i].getLabel(), headerStyle);
                sheet.setColumnWidth(i, 25 * 256);
            }

            for (int i = 0; i < areas.size(); i++) {
                String label = "QTE " + areas.get(i);
                createCell(row, indexResolver.getStorageAreaColumnIndex(i), label, headerStyle);
                sheet.autoSizeColumn(indexResolver.getStorageAreaColumnIndex(i));
            }

            for (int i = 3; i < Header.values().length; i++) {
                int colIdx = indexResolver.getFixedHeaderColumnIndexAfterArea(i - 3);
                createCell(row, colIdx, Header.values()[i].getLabel(), headerStyle);
                sheet.setColumnWidth(colIdx, 25 * 256);
            }
            logger.info("Header row created successfully.");
        } catch (Exception e) {
            logger.error("Error creating header row : {}", e.getMessage(), e);
            throw new RowCreationException("Failed to create header row");
        }
    }

    /**
     * Creates a row in the sheet representing a single stock registration.
     *
     * @param sheet the sheet to write to
     * @param lineNumber the row number
     * @param registration the stock registration to write
     * @throws RowCreationException if data is invalid or cannot be written
     */
    public void createStockRow(Sheet sheet, int lineNumber, StockRegistrationForReport registration) throws RowCreationException {
        String comment;
        String categoryName;
        try {
            categoryName = registration.getCategoryName();
        } catch (WarningStockDataException e) {
            categoryName = "no category";
        }
        logger.info("Start creating registration row for category '{}' at line {}", categoryName, lineNumber);

        try {
            Row row = sheet.createRow(lineNumber);
            row.setHeightInPoints(40);
            CellStyle defaultCellStyle = cellStyleFactory.getDefaultCellStyle();
            CellStyle priceCellStyle = cellStyleFactory.getPriceCellStyle();
            CellStyle totalPriceCellStyle = cellStyleFactory.getTotalPriceCellStyle();
            CellStyle quantityCellStyle = cellStyleFactory.getQuantityCellStyle();

            String totalQtyFormula = getTotalQtyFormula(lineNumber, registration.getCalculationRule());
            String totalPriceFormula = getTotalPriceFormula(lineNumber);
            createCell(row, indexResolver.getReferenceColumn(), registration.getReferenceName(), defaultCellStyle);
            for (int i = 0; i < areas.size(); i++) {
                double qty = registration.getQuantityFromArea(areas.get(i));
                createCell(row, indexResolver.getStorageAreaColumnIndex(i), qty, quantityCellStyle);
                logger.debug("Added quantity for area '{}': {}", areas.get(i), qty);
            }
            createCellFormula(row, indexResolver.getTotalQuantityColumn(), totalQtyFormula, quantityCellStyle);

            createCellFormula(row, indexResolver.getTotalPriceColumn(), totalPriceFormula, totalPriceCellStyle);
            comment = registration.getComment() + "\r\n" + createCellsWithWarning(row,registration);
            createCommentCell(row,indexResolver.getCommentColumn(),comment, sheet);

            logger.info("Registration row created successfully for category '{}' at line {}", categoryName, lineNumber);
        } catch (InvalidStockDataException e) {
            throw new RowCreationException("Impossible de créer la ligne de stock : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating registration row at line {} for category '{}': {}", lineNumber, categoryName, e.getMessage(), e);
            throw new RowCreationException("Impossible de créer la ligne de stock");
        }
    }

    /**
     * Creates a cell containing comment text, auto-adjusting row height to fit the comment.
     */
    private void createCommentCell(Row row, int commentColumn, String comment, Sheet sheet) {
        CellStyle commentCellStyle = cellStyleFactory.getCommentCellStyle();
        createCell(row,commentColumn,comment, commentCellStyle);
        sheet.autoSizeColumn(commentColumn);
        int maxWidth = sheet.getColumnWidth(commentColumn) / 256;
        int textLength = comment.length();
        int linesNeeded = (int) Math.ceil((double) textLength / maxWidth);
        if(linesNeeded>0) {
            row.setHeightInPoints(linesNeeded * sheet.getDefaultRowHeightInPoints());
        }
    }

    /**
     * Creates the footer row at the specified line, displaying the total value.
     */
    public void createFooterRow(Sheet sheet, int lineNumber) {
        Row row = sheet.createRow(lineNumber);
        CellStyle rowStyle = cellStyleFactory.getDefaultCellStyle();
        createCell(row, indexResolver.getPriceColumn(),"Total",rowStyle);
        String totalPrice = getTotalPriceFormulaSheet(lineNumber);
        createCellFormula(row, indexResolver.getTotalPriceColumn(), totalPrice, rowStyle);
    }

    /**
     * Writes cells that may contain warnings (e.g. missing data), capturing messages into a comment.
     */
    private String createCellsWithWarning(Row row, StockRegistrationForReport registration){
        StringBuilder comment = new StringBuilder();
        try {
            createCell(row, indexResolver.getCategoryColumn(), registration.getCategoryName(), cellStyleFactory.getCategoryCellStyle());
        } catch (WarningStockDataException e) {
            createCell(row, indexResolver.getCategoryColumn(),"", cellStyleFactory.getCategoryCellStyle());
            comment.append(e.getMessage()).append("\r\n");
        }
        try {
            createCell(row, indexResolver.getProviderColumn(), registration.getProvider(), cellStyleFactory.getDefaultCellStyle());
        } catch (WarningStockDataException e) {
            createCell(row, indexResolver.getProviderColumn(), "", cellStyleFactory.getDefaultCellStyle());
            comment.append(e.getMessage()).append("\r\n");
        }
        try {
            createCell(row, indexResolver.getUnitColumn(), registration.getUnity(), cellStyleFactory.getDefaultCellStyle());
        } catch (WarningStockDataException e) {
            createCell(row, indexResolver.getUnitColumn(), "", cellStyleFactory.getDefaultCellStyle());
            comment.append(e.getMessage()).append("\r\n");
        }
        try{
            createCell(row, indexResolver.getPriceColumn(), BigDecimal.valueOf(registration.getPrice()).doubleValue(), cellStyleFactory.getPriceCellStyle());
            if(registration.getRefFacPrice() != null){
                comment.append("Le prix utilisé est issu du numéro de pièce : ").append(registration.getRefFacPrice()).append("\r\n");
            }else{
                comment.append("Le prix utilisé est issu de la saisie d'un utilisateur").append("\r\n");
            }
        }catch (InvalidStockDataException e){
            createCell(row, indexResolver.getPriceColumn(), 0.0, cellStyleFactory.getDefaultCellStyle());
            comment.append(e.getMessage()).append("\r\n");
        }

        return comment.toString();
    }

    private String getTotalPriceFormula(int lineNumber) {
        return String.format("%s%d*%s%d",
                CellReference.convertNumToColString(indexResolver.getPriceColumn()), lineNumber + 1,
                CellReference.convertNumToColString(indexResolver.getTotalQuantityColumn()), lineNumber + 1
        );
    }

    private String getTotalQtyFormula(int lineNumber, String convertFormula) {
        String sumFormula = String.format("SUM(%s%d:%s%d)",
                CellReference.convertNumToColString(indexResolver.getFirstStorageAreaColumn()),
                lineNumber + 1,
                CellReference.convertNumToColString(indexResolver.getLastStorageAreaColumn()),
                lineNumber + 1
        );

        if (convertFormula != null && !convertFormula.isEmpty()) {
            String sanitizedFormula = convertFormula.replace(",", ".");
            return String.format("(%s)%s", sumFormula, sanitizedFormula);
        }

        return sumFormula;
    }

    private String getTotalPriceFormulaSheet(int lineNumber) {
        return String.format("SUM(%s%d:%s%d)",
                CellReference.convertNumToColString(indexResolver.getTotalPriceColumn()),
                2,
                CellReference.convertNumToColString(indexResolver.getTotalPriceColumn()),
                lineNumber);
    }


    private void createCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        cell.setCellStyle(style);
    }

    private void createCellFormula(Row row, int columnIndex, String formula, CellStyle style) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellFormula(formula);
        cell.setCellStyle(style);
    }

    /**
     * Merges consecutive category cells if they contain the same value.
     */
    public void mergeConsecutiveCategoryCells(Sheet sheet, int lastDataRowIndex) {
        int startRow = 1;
        int categoryColumnIndex = indexResolver.getCategoryColumn();
        String previousCategory = getCellValueAsString(sheet.getRow(startRow).getCell(categoryColumnIndex));

        for (int rowIndex = 2; rowIndex <= lastDataRowIndex; rowIndex++) {
            Row currentRow = sheet.getRow(rowIndex);
            if (currentRow == null) continue;

            String currentCategory = getCellValueAsString(currentRow.getCell(categoryColumnIndex));

            if (!currentCategory.equals(previousCategory)) {
                if (rowIndex - 1 > startRow) {
                    sheet.addMergedRegion(new CellRangeAddress(startRow, rowIndex - 1, categoryColumnIndex, categoryColumnIndex));
                    logger.debug("Fusion de la catégorie '{}' de la ligne {} à {}", previousCategory, startRow + 1, rowIndex);
                }
                // Nouveau groupe
                startRow = rowIndex;
                previousCategory = currentCategory;
            }
        }

        // Dernier groupe à fusionner
        if (lastDataRowIndex > startRow) {
            sheet.addMergedRegion(new CellRangeAddress(startRow, lastDataRowIndex, categoryColumnIndex, categoryColumnIndex));
            logger.debug("Fusion finale de la catégorie '{}' de la ligne {} à {}", previousCategory, startRow + 1, lastDataRowIndex + 1);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
