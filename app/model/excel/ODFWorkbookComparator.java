package model.excel;

import org.odftoolkit.simple.SpreadsheetDocument;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class ODFWorkbookComparator {
	
	private SpreadsheetDocument sdMaster;
	private SpreadsheetDocument sdCompare;
	private String message;
	
	public ODFWorkbookComparator(SpreadsheetDocument sd1, SpreadsheetDocument sd2) {
		this.sdMaster = sd1;
		this.sdCompare = sd2;
		this.message = "";
	}
	
	public SpreadsheetDocument getMasterWorkbook() {
		return this.sdMaster;
	}
	
	public SpreadsheetDocument getCompareWorkbook() {
		return this.sdCompare;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public boolean compareSheetNum() {
		if (this.sdMaster.getSheetCount() != this.sdCompare.getSheetCount()) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean compareSheetCharts() {
		int count1 = this.sdMaster.getChartCount();
		int count2 = this.sdCompare.getChartCount();
		if (0 < count1) {
			if (count2 == 0) {
				this.message = "Diagramm falsch. Kein Diagramm im Dokument gefunden.";
			} else if (count1 != count2) {
				this.message = "Diagramm falsch. Zu wenig Diagramme im Dokument (Erwartet: " + count1 + ").";
				
			}
			return true;
		}
		return false;
	}
	
	public void closeWorkbooks() {
		this.sdMaster.close();
		this.sdCompare.close();
	}

}
