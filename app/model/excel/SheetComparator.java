package model.excel;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;

import general.HashSetHelper;
import general.RegExpHelper;
import general.StringHelper;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class SheetComparator {

	private Sheet shMaster;
	private Sheet shCompare;
	private String message;
	
	public SheetComparator(Sheet sheet1, Sheet sheet2) {
		this.shMaster = sheet1;
		this.shCompare = sheet2;
		this.message = "";
	}
	
	public Sheet getSheetCompare() {
		return this.shCompare;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public boolean compareSheetConditionalFormatting() {
		SheetConditionalFormatting scf1 = this.shMaster.getSheetConditionalFormatting();
		SheetConditionalFormatting scf2 = this.shCompare.getSheetConditionalFormatting();
		int count1 = scf1.getNumConditionalFormattings();
		int count2 = scf2.getNumConditionalFormattings();
		if (0 < count1) {
			if (count2 == 0) {
				this.message = "Bedingte Formatierung falsch. Keine Bedingte Formatierung gefunden.";
			} else if (count1 != count2) {
				this.message = "Bedingte Formatierung falsch. Zu wenig Bedingte Formatierungen (Erwartet: " + count1 + ").";
			} else {
				for (int i = 0; i < count1; i++) {
					ConditionalFormatting format1 = scf1.getConditionalFormattingAt(i);
					ConditionalFormatting format2 = scf2.getConditionalFormattingAt(i);
					if (format2 != null) {
						if (format1.equals(format2)) {
							this.message = "Bedingte Formatierung richtig.";
						} else {
							this.message = "Bedingte Formatierung falsch.";
							// Compare ranges reference
							HashSet<String> items1 = new HashSet<String>();
							for (CellRangeAddress cf : format1.getFormattingRanges()) {
								items1.add(cf.formatAsString());
							}
							HashSet<String> items2 = new HashSet<String>();
							for (CellRangeAddress cf : format2.getFormattingRanges()) {
								items2.add(cf.formatAsString());
							}
							String cfDiff = HashSetHelper.getSheetCFDiff(items2, items1);
							if (cfDiff != "") {
								this.message += " Der Bereich " + cfDiff + " ist falsch.";
							} else {
								String string1 = RegExpHelper.getExcelCFFormulaList(format1.toString());
								String string2 = RegExpHelper.getExcelCFFormulaList(format2.toString());
								this.message += StringHelper.getDiffOfTwoFormulas(string1, string2);
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean compareSheetCharts() {
		XSSFDrawing drawing1 = ((XSSFSheet)this.shMaster).createDrawingPatriarch();
		XSSFDrawing drawing2 = ((XSSFSheet)this.shCompare).createDrawingPatriarch();
		int count1 = drawing1.getCharts().size();
		int count2 = drawing2.getCharts().size();
		if (0 < count1) {
			if (count2 == 0) {
				this.message = "Diagramm falsch. Kein Diagramm gefunden.";
			} else if (count1 != count2) {
				this.message = "Diagramm falsch. Zu wenig Diagramme (Erwartet: " + count1 + ").";
			} else {
				for (int i = 0; i < count1; i++) {
					CTChart chartMaster = drawing1.getCharts().get(i).getCTChart();
					CTChart chartCompare = drawing2.getCharts().get(i).getCTChart();
					if (chartCompare != null) {
						this.message = "Diagramm falsch.";
						String stringMaster = chartMaster.toString();
						String stringCompare = chartCompare.toString();
						// Compare Title
						String title1 = RegExpHelper.getExcelChartTitle(stringMaster);
						String title2 = RegExpHelper.getExcelChartTitle(stringCompare);
						if (!title1.equals(title2)) {
							this.message += " Der Titel sollte " + title1 + " lauten.";
						} else {
							// Compare ranges
							String chDiff = RegExpHelper.
									getExcelChartRangesDiff(this.shMaster.getSheetName(), stringMaster,
											this.shCompare.getSheetName(), stringCompare);
							if (chDiff != "") {
								this.message += " Der Bereich " + chDiff + " ist falsch.";
							} else {
								this.message = "Diagramm richtig.";
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}
	
	public ArrayList<Cell> getColoredRange() {
		ArrayList<Cell> range = new ArrayList<Cell>();
		for (Row row : this.shMaster) {
			for (Cell cell : row) {
				Color foreground = cell.getCellStyle().getFillForegroundColorColor();
				Color background = cell.getCellStyle().getFillBackgroundColorColor();
				if (foreground != null && background != null) {
					range.add(cell);
				}
			}
		}
		return range;
	}
	
	public ArrayList<Cell> getColoredRange(XSSFColor color) {
		ArrayList<Cell> range = new ArrayList<Cell>();
		for (Row row : this.shMaster) {
			for (Cell cell : row) {
				XSSFColor foreground = (XSSFColor) cell.getCellStyle().getFillForegroundColorColor();
				XSSFColor background = (XSSFColor) cell.getCellStyle().getFillBackgroundColorColor();
				if (foreground != null && background != null) {
					if (foreground.getARGBHex() == color.getARGBHex() || background.getARGBHex() == color.getARGBHex()) {
						range.add(cell);
					}
				}
			}
		}
		return range;
	}

}
