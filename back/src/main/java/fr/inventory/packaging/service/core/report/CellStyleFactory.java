package fr.inventory.packaging.service.core.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Factory class for creating and caching commonly used cell styles
 * for Excel report generation.
 */
public class CellStyleFactory {

    private final XSSFWorkbook workbook;
    private final CategoryColorGenerator colorGenerator;

    private final CellStyle defaultCellStyle;
    private final CellStyle headerCellStyle;
    private final CellStyle commentCellStyle;
    private final CellStyle priceCellStyle;
    private final CellStyle totalPriceCellStyle;
    private final CellStyle quantityCellStyle;


    public CellStyleFactory(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.colorGenerator = new CategoryColorGenerator();

        this.defaultCellStyle = createDefaultCellStyle();
        this.headerCellStyle = createHeaderCellStyle();
        //this.categoryCellStyle = createCategoryStyle();
        this.commentCellStyle = createCommentStyle();
        this.priceCellStyle = createPriceCellStyle();
        this.totalPriceCellStyle = createTotalPriceCellStyle();
        this.quantityCellStyle = createQuantityCellStyle();
    }

    public CellStyle getDefaultCellStyle() {
        return defaultCellStyle;
    }

    public CellStyle getHeaderCellStyle() {
        return headerCellStyle;
    }

    public CellStyle getCategoryCellStyle() {
        CellStyle style = workbook.createCellStyle();
        style.cloneStyleFrom(defaultCellStyle);
        style.setFillForegroundColor(colorGenerator.getNextColor());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public CellStyle getCommentCellStyle() {
        return commentCellStyle;
    }

    public CellStyle getPriceCellStyle() {
        return priceCellStyle;
    }

    public CellStyle getTotalPriceCellStyle() {
        return totalPriceCellStyle;
    }

    public CellStyle getQuantityCellStyle() {
        return quantityCellStyle;
    }

    private CellStyle createDefaultCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    private CellStyle createHeaderCellStyle() {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THICK);
        headerStyle.setBorderBottom(BorderStyle.THICK);
        headerStyle.setBorderLeft(BorderStyle.THICK);
        headerStyle.setBorderRight(BorderStyle.THICK);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        headerStyle.setFont(font);
        return headerStyle;
    }

    private CellStyle createCommentStyle(){
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(defaultCellStyle);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    private CellStyle createPriceCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(defaultCellStyle);
        DataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("# ##0.0#### [$€-fr-FR]"));

        return  cellStyle;
    }

    private CellStyle createTotalPriceCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(defaultCellStyle);
        DataFormat format = workbook.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("# ##0.0#### [$€-fr-FR]"));

        return  cellStyle;
    }

    private CellStyle createQuantityCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();

        cellStyle.cloneStyleFrom(defaultCellStyle);
        cellStyle.setDataFormat(format.getFormat("# ##0"));

        return  cellStyle;
    }

}
