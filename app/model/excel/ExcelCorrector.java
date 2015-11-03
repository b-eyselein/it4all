package model.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class ExcelCorrector {
	
	static final String DIR = "files/";
	
	private String userFolder;
	private String filePath;
	private String fileName = "Aufgabe_Einfuehrung";
	private String fileExtension;
	
	public ExcelCorrector(String user, String path) {
		this.userFolder = user + "/";
		this.filePath = path;
		this.setExtension(path);
	}
	
	public void startComparison(boolean conditionalFormating, boolean charts) {
		// TODO: static
		try {
			// Create workbook of master file
			Workbook wbMaster = this.getWorkbookOfPath(DIR + fileName + "_Muster." + this.fileExtension);
			// Create workbook of file to test
			Workbook wbCompare = this.getWorkbookOfPath(this.filePath);
			// Create WorkbookComparison
			WorkbookComparator wc = new WorkbookComparator(wbMaster, wbCompare);
			if (!wc.compareSheetNum()) {
				// TODO
			}
			if (wbMaster != null) {
				// Iterate over sheets
				for (Sheet shMaster : wc.getMasterWorkbook()) {
					int index = shMaster.getWorkbook().getSheetIndex(shMaster);
					Sheet shCompare = wc.getCompareWorkbook().getSheetAt(index);
					if (shCompare != null) {
						// Create SheetComparator
						SheetComparator sc = new SheetComparator(shMaster, shCompare);
						// Compare conditional formatting
						if (conditionalFormating && sc.compareSheetConditionalFormatting()) {
							SheetStyler.setSheetComment(shCompare, sc.getMessage(), 0, 0);
						}
						// Compare charts
						if (charts && sc.compareSheetCharts()) {
							// TODO: Merge message of cf and chart
							SheetStyler.setSheetComment(shCompare, sc.getMessage(), 0, 0);
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
									CellComparator cc = new CellComparator(cellMaster, cellCompare);
									// Compare cell values
									boolean equalCell = cc.compareCellValues();
									boolean equalFormula = cc.compareCellFormulas();
									CellStyler.setCellComment(cellCompare, cc.getMessage());
									if (cellCompare.toString().equals("")) {
										// Style yellow
										CellStyler.setCellStyle(cellCompare, IndexedColors.YELLOW);
									} else {
										if (equalCell && equalFormula) {
											// Style green
											CellStyler.setCellStyle(cellCompare, IndexedColors.GREEN);
										} else {
											// Style red
											CellStyler.setCellStyle(cellCompare, IndexedColors.RED);
										}
									}
								}
							}
						}
					} else {
						System.out.println("Das zu testende Sheet konnte nicht geöffnet werden.");
					}
				}
			} else {
				System.out.println("Mastersheet konnte nicht geöffnet werden.");
			}
			// Save and close
			this.saveWorkbook(wc.getCompareWorkbook());
			wc.closeWorkbooks();
		} catch (Exception e) {
			// TODO: Error file?
			System.out.println("Fehler: " + e.getMessage());
		}
	}
	
	private void saveWorkbook(Workbook wb) throws Exception {
		File dir = new File(DIR + this.userFolder);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileOutputStream fileOut = new FileOutputStream(DIR + this.userFolder +
										this.fileName + "_Korrektur." + this.fileExtension);
	    wb.write(fileOut);
	    fileOut.close();
	}
	
	private void setExtension(String string) {
		this.fileExtension = string.substring(string.lastIndexOf(".") + 1).trim().toLowerCase();
	}
	
	private Workbook getWorkbookOfPath(String path) throws Exception {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		Workbook wb = null;
		switch (this.fileExtension) {
		case ("xlsx"):
			wb = new XSSFWorkbook(fis);
			break;
		case ("xlsm"):
			wb = new XSSFWorkbook(fis);
			break;
		case "xls":
			wb = new HSSFWorkbook(fis);
			break;
		case "ods":
			// TODO: odf toolkit
			break;
		}
		fis.close();
		return wb;
	}

	public static void main(String[] args) {
		ExcelCorrector comp = new ExcelCorrector("test", DIR + "Aufgabe_Einfuehrung.xlsm");
		comp.startComparison(true, true);
		System.out.println("Done!");
	}

}
