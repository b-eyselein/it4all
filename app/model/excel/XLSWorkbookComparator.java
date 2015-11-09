package model.excel;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class XLSWorkbookComparator {
	
	private Workbook wbMaster;
	private Workbook wbCompare;
	
	public XLSWorkbookComparator(Workbook wb1, Workbook wb2) {
		this.wbMaster = wb1;
		this.wbCompare = wb2;
	}
	
	public Workbook getMasterWorkbook() {
		return this.wbMaster;
	}
	
	public Workbook getCompareWorkbook() {
		return this.wbCompare;
	}
	
	public boolean compareSheetNum() {
		if (this.wbMaster.getNumberOfSheets() != this.wbCompare.getNumberOfSheets()) {
			return false;
		} else {
			return true;
		}
	}
	
	public void closeWorkbooks() {
		try {
			this.wbMaster.close();
			this.wbCompare.close();
		} catch (IOException e) {
			// Do nothing
		}
	}

}
