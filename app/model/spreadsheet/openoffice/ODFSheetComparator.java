package model.spreadsheet.openoffice;

import java.util.ArrayList;

import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class ODFSheetComparator {
	
	private static final int MAXROW = 80;
	private static final int MAXCOLUMN = 22;
	
	private Table tMaster;
	private Table tCompare;
	private String message;
	
	public ODFSheetComparator(Table table1, Table table2) {
		this.tMaster = table1;
		this.tCompare = table2;
		this.message = "";
	}
	
	public Table getSheetCompare() {
		return this.tCompare;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	@SuppressWarnings("deprecation")
	public ArrayList<Cell> getColoredRange() {
		ArrayList<Cell> range = new ArrayList<Cell>();
		for (int row = 0; row < MAXROW; row++) {
			Row oRow = this.tMaster.getRowByIndex(row);
			for (int column = 0; column < MAXCOLUMN; column++) {
				Cell oCell = oRow.getCellByIndex(column);
				String color = oCell.getCellBackgroundColorString();
				if (!color.equals("#FFFFFF")) {
					range.add(oCell);
				}
			}
		}
		return range;
	}

}
