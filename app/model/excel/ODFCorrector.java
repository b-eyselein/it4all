package model.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class ODFCorrector {
	
	public static String startComparison(String musterPath, String testPath, boolean conditionalFormating, boolean charts) {
		String notice = "";
		try {
			String userFolder = ExcelCorrector.getUserFolder(testPath);
			String fileName = ExcelCorrector.getFileName(testPath);
			// Create workbook of master file
			SpreadsheetDocument sdMaster = ODFCorrector.getSpreadsheetOfPath(ExcelCorrector.DIR + "muster/" + fileName + "_Muster.ods");
			// Create workbook of file to test
			SpreadsheetDocument sdCompare = ODFCorrector.getSpreadsheetOfPath(testPath);
			// Create WorkbookComparison
			ODFWorkbookComparator wc = new ODFWorkbookComparator(sdMaster, sdCompare);
			// Compare sheet number
			if (!wc.compareSheetNum()) {
				notice = "Sie haben die falsche Datei hochgeladen.";
				return notice;
			}
			if (sdMaster != null) {
				// Iterate over sheets
				int sCount = sdMaster.getSheetCount();
				for (int index = 0; index < sCount; index++) {
					Table tMaster = sdMaster.getSheetByIndex(index);
					Table tCompare = wc.getCompareWorkbook().getSheetByIndex(index);
					// Compare charts
					if (index == 0 && charts) {
						wc.compareSheetCharts();
						SheetStyler.setODFSheetComment(tCompare, wc.getMessage(), 0, 0);
					}
					if (tCompare != null) {
						// Create SheetComparator
						ODFSheetComparator sc = new ODFSheetComparator(tMaster, tCompare);
						// Compare conditional formatting
						if (conditionalFormating) {
							// NOTICE: Does not work in ODF Toolkit
						}
						// Iterate over colored cells
						ArrayList<Cell> range = sc.getColoredRange();
						for (Cell cellMaster : range) {
							int rowIndex = cellMaster.getRowIndex();
							int columnIndex = cellMaster.getColumnIndex();
							Row rowCompare = sc.getSheetCompare().getRowByIndex(rowIndex);
							if (rowCompare != null) {
								Cell cellCompare = rowCompare.getCellByIndex(columnIndex);
								if (cellCompare != null) {
									// Create CellComparator
									ODFCellComparator cc = new ODFCellComparator(cellMaster, cellCompare);
									cellCompare.setNoteText(null);
									// Compare cell values
									boolean equalCell = cc.compareCellValues();
									boolean equalFormula = cc.compareCellFormulas();
									CellStyler.setODFCellComment(cellCompare, cc.getMessage());
									if (equalCell && equalFormula) {
										// Style green
										CellStyler.setODFCellStyle(cellCompare, true);
									} else {
										// Style red
										CellStyler.setODFCellStyle(cellCompare, false);
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
			ODFCorrector.saveSpreadsheet(wc.getCompareWorkbook(), userFolder, fileName);
			wc.closeWorkbooks();
			return notice;
		} catch (Exception e) {
			notice = "Es ist ein unerwarteter Fehler aufgetreten. Bitte wenden Sie sich an den Übungsleiter.";
			return notice;
		}
	}
	
	private static void saveSpreadsheet(SpreadsheetDocument sd, String userFolder, String fileName) throws Exception {
		File dir = new File(userFolder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileOutputStream fileOut = new FileOutputStream(userFolder +
										fileName + "_Korrektur.ods");
	    sd.save(fileOut);
	    fileOut.close();
	}
	
	private static SpreadsheetDocument getSpreadsheetOfPath(String path) throws Exception {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		SpreadsheetDocument sd = SpreadsheetDocument.loadDocument(fis);
		fis.close();
		return sd;
	}

}
