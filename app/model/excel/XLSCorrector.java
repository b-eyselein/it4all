package model.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class XLSCorrector {
	
	public static String startComparison(String musterPath, String testPath, boolean conditionalFormating, boolean charts) {
		String notice = "";
		try {
			String userFolder = ExcelCorrector.getUserFolder(testPath);
			String fileName = ExcelCorrector.getFileName(testPath);
			String extension = ExcelCorrector.getExtension(testPath);
			// Create workbook of master file
			Workbook wbMaster = XLSCorrector.getWorkbookOfPath(ExcelCorrector.DIR + "muster/" + fileName + "_Muster." + extension);
			// Create workbook of file to test
			Workbook wbCompare = XLSCorrector.getWorkbookOfPath(testPath);
			// Create WorkbookComparison
			XLSWorkbookComparator wc = new XLSWorkbookComparator(wbMaster, wbCompare);
			// Compare sheet number
			if (!wc.compareSheetNum()) {
				notice = "Sie haben die falsche Datei hochgeladen.";
				return notice;
			}
			if (wbMaster != null) {
				// Iterate over sheets
				for (Sheet shMaster : wc.getMasterWorkbook()) {
					int index = shMaster.getWorkbook().getSheetIndex(shMaster);
					Sheet shCompare = wc.getCompareWorkbook().getSheetAt(index);
					if (shCompare != null) {
						// Create SheetComparator
						XLSSheetComparator sc = new XLSSheetComparator(shMaster, shCompare);
						// Compare conditional formatting
						if (conditionalFormating) {
							sc.compareSheetConditionalFormatting();
						}
						// Compare charts
						if (charts) {
							sc.compareSheetCharts();
						}
						if (conditionalFormating || charts) {
							SheetStyler.setXLSSheetComment(shCompare, sc.getMessage(), 0, 0);
						}
						// Iterate over colored cells
						ArrayList<Cell> range = sc.getColoredRange();
						for (Cell cellMaster : range) {
							int rowIndex = cellMaster.getRowIndex();
							int columnIndex = cellMaster.getColumnIndex();
							Row rowCompare = sc.getSheetCompare().getRow(rowIndex);
							if (rowCompare != null) {
								Cell cellCompare = rowCompare.getCell(columnIndex);
								if (cellCompare != null) {
									// Create CellComparator
									XLSCellComparator cc = new XLSCellComparator(cellMaster, cellCompare);
									cellCompare.setCellComment(null);
									// Compare cell values
									boolean equalCell = cc.compareCellValues();
									boolean equalFormula = cc.compareCellFormulas();
									CellStyler.setXLSCellComment(cellCompare, cc.getMessage());
									if (equalCell && equalFormula) {
										// Style green
										CellStyler.setXLSCellStyle(cellCompare, true);
									} else {
										// Style red
										CellStyler.setXLSCellStyle(cellCompare, false);
									}
								}
							}
						}
					} else {
						notice = "Ihre Upload-Datei konnte nicht geöffnet werden.";
					}
				}
			} else {
				notice = "Der Test konnte nicht gestartet werden.";
			}
			// Save and close
			XLSCorrector.saveWorkbook(wc.getCompareWorkbook(), userFolder, fileName, extension);
			wc.closeWorkbooks();
			return notice;
		} catch (Exception e) {
			notice = "Es ist ein unerwarteter Fehler aufgetreten. Bitte wenden Sie sich an den Übungsleiter.";
			return notice;
		}
	}
	
	private static void saveWorkbook(Workbook wb, String userFolder, String fileName, String extension) throws Exception {
		File dir = new File(userFolder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileOutputStream fileOut = new FileOutputStream(userFolder +
										fileName + "_Korrektur." + extension);
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	private static Workbook getWorkbookOfPath(String path) throws IOException {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		Workbook wb = null;
		switch (ExcelCorrector.getExtension(path)) {
		case "xlsx":
			wb = new XSSFWorkbook(fis);
			break;
		case "xlsm":
			wb = new XSSFWorkbook(fis);
			break;
		case "xls":
			wb = new HSSFWorkbook(fis);
			break;
		}
		fis.close();
		return wb;
	}

}
