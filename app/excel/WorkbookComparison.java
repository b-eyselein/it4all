package excel;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ChartsheetDocument;

public class WorkbookComparison {
	
	static final String DIR = "files/";
	
	private String fileAdjust;
	private Workbook wbMaster;
	private Workbook wbTest;

	/**
	 * @param wbTest
	 */
	public WorkbookComparison(String sTest) throws Exception {
		try {
			File fileTest = new File(sTest);
			String fullName = fileTest.getName();
			// Get file extension and name separate
			String extension = fullName.substring(fullName.lastIndexOf(".") + 1);
			String sName = fullName.substring(0, fullName.lastIndexOf("."));
			this.fileAdjust = sName + "_Korrektur." + extension;
			if (extension == "") {
				throw new Exception("Dateiformat ist nicht spezifiziert.");
			}
			// Create workbook of file to test
			FileInputStream fisTest = new FileInputStream(fileTest);
			this.wbTest = new XSSFWorkbook(fisTest);
			fisTest.close();
			// Create workbook of master file
			File fileMaster = new File(DIR + sName + "_Muster." + extension);
			FileInputStream fisMaster = new FileInputStream(fileMaster);
			this.wbMaster = new XSSFWorkbook(fisMaster);
			fisMaster.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void closeWorkbooks() {
		try {
			this.wbMaster.close();
			FileOutputStream fileOut = new FileOutputStream(DIR + this.fileAdjust);
		    this.wbTest.write(fileOut);
		    fileOut.close();
			this.wbTest.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void startComparison() {
		for (Sheet shMaster : wbMaster) {
			String shName = shMaster.getSheetName();
			Sheet shTest = this.wbTest.getSheet(shName);
			ArrayList<Cell> range = this.getColoredRangeOfSheet(shMaster, new XSSFColor(Color.yellow));
			// Compare charts
			boolean chartsEqual = this.compareSheetCharts(shMaster, shTest);
			// Compare conditional formatting
			// TODO: functionize
			System.out.println(shMaster.getSheetConditionalFormatting().getConditionalFormattingAt(0).getRule(0).getFormula1());
			// Iterate over yellow colored cells and compare
			for (Cell cellMaster : range) {
				int rowIndex = cellMaster.getRowIndex();
				int columnIndex = cellMaster.getColumnIndex();
				Cell cellTest = shTest.getRow(rowIndex).getCell(columnIndex);
				// Clear comment if exists
				if (cellTest.getCellComment() != null) {
					cellTest.removeCellComment();
				}
				// Compare cell values
				boolean valuesEqual = this.compareCellValues(cellMaster, cellTest);
				// Compare cell formulas
				boolean formulasEqual = this.compareCellFormulas(cellMaster, cellTest);
				// Set background color of cell
				this.setEvaluatedBackgroundColor(cellTest, (valuesEqual && formulasEqual));
			}
		}
	}
	
	private boolean compareSheetCharts(Sheet sheet1, Sheet sheet2) {
		XSSFDrawing drawing = ((XSSFSheet)sheet1).createDrawingPatriarch();
		// TODO: get chart data
		for (XSSFChart chart : drawing.getCharts()) {
			System.out.println(chart.getCTChart().toString());
		}
		return false;
	}
	
	/**
	 * 
	 * @param cell1
	 * @param cell2
	 * @return
	 */
	private boolean compareCellValues(Cell cell1, Cell cell2) {
		String cell1Value = this.getStringValueOfCell(cell1);
		String cell2Value = this.getStringValueOfCell(cell2);
		String headline = "Wert:\n";
		String message = "";
		if (!cell1Value.equals(cell2Value)) {
			message = "Inkorrekt. Erwartet wurde '" + cell1Value + "'.";
			this.setFormattedCellComment(cell2, headline, message);
			return false;
		} else {
			message = "Korrekt.";
			this.setFormattedCellComment(cell2, headline, message);
			return true;
		}
	}
	
	/**
	 * 
	 * @param cell1
	 * @param cell2
	 * @return
	 */
	private boolean compareCellFormulas(Cell cell1, Cell cell2) {
		String headline = "";
		String message = "";
		if(cell1.getCellType() == Cell.CELL_TYPE_FORMULA) {
			headline = "Formel:\n";
			if (cell1.toString().equals(cell2.toString())) {
				message = "Korrekt.";
				this.setFormattedCellComment(cell2, headline, message);
				return true;
			} else if (cell2.getCellType() != Cell.CELL_TYPE_FORMULA) {
				message = "Inkorrekt. Es sollte eine Formel benutzt werden.\n";
				Pattern p = Pattern.compile(".*?([A-Z]+).*");
				Matcher m = p.matcher(cell1.getCellFormula());
				if (m.matches()) {
					message += "(Hinweis: Versuchen Sie es mit " + m.group(1) +")";
				}
				this.setFormattedCellComment(cell2, headline, message);
				return false;
			} else {
				message = "Inkorrekt.\n";
				Pattern p = Pattern.compile(".*?([A-Z]+[0-9]+).*?");
				Matcher m = p.matcher(cell1.getCellFormula());
				ArrayList<String> matches = new ArrayList<String>();
				while (m.find()) {
					matches.add(m.group(1));
				}
				message += "(Hinweis: Haben Sie an den Bereich ";
				for (int i = 0; i < matches.size(); i++) {
					message += matches.get(i);
					if (i == (matches.size() - 2)) {
						message += " und ";
					} else if (i < (matches.size() - 2)) {
						message += ", ";
					}
				}
				message +=  " gedacht?)";
				this.setFormattedCellComment(cell2, headline, message);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param cell
	 * @param equal
	 */
	private void setEvaluatedBackgroundColor(Cell cell, boolean equal) {
		// TODO: color does not change
		/*if (cell.toString() != "") {
			CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
			style.cloneStyleFrom(cell.getCellStyle());
			if (equal) {
			    style.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
			} else {
			    style.setFillBackgroundColor(IndexedColors.RED.getIndex());
			}
			style.setFillPattern(CellStyle.ALIGN_FILL);
		    cell.setCellStyle(style);
		}*/
	}
	
	/**
	 * 
	 * @param cell
	 * @return
	 */
	private String getStringValueOfCell(Cell cell) {
		String value = cell.toString();
		if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
	        switch(cell.getCachedFormulaResultType()) {
	            case Cell.CELL_TYPE_NUMERIC:
	            	value = Double.toString(cell.getNumericCellValue());
	                break;
	            case Cell.CELL_TYPE_STRING:
	            	value = cell.getRichStringCellValue().toString();
	                break;
	        }
	     }
		return value;
	}
	
	private void setFormattedCellComment(Cell cell, String headline, String message) {
		Drawing drawing = cell.getSheet().createDrawingPatriarch();
		CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
		// Create comment space
		ClientAnchor anchor = factory.createClientAnchor();
	    anchor.setCol1(cell.getColumnIndex());
	    anchor.setCol2(cell.getColumnIndex() + 5);
	    anchor.setRow1(cell.getRowIndex());
	    anchor.setRow2(cell.getRowIndex() + 5);
	    // Insert new comment if not exists
	    String overhead = "";
	    Comment comment = null;
	    if (cell.getCellComment() != null) {
			overhead = cell.getCellComment().getString().toString() + "\n";
			comment = cell.getCellComment();
		} else {
			comment = drawing.createCellComment(anchor);
		}
	    RichTextString str = factory.createRichTextString(overhead + headline + message);
	    comment.setVisible(false);
	    comment.setString(str);
	    // Set comment
	    cell.setCellComment(comment);;
	}
	
	/**
	 * 
	 * @param sheet
	 * @param color
	 * @return
	 */
	private ArrayList<Cell> getColoredRangeOfSheet(Sheet sheet, XSSFColor color) {
		ArrayList<Cell> range = new ArrayList<Cell>();
		for (Row row : sheet) {
			for (Cell cell : row) {
				XSSFColor background = (XSSFColor) cell.getCellStyle().getFillBackgroundColorColor();
				if (background != null && background.getARGBHex().equals(color.getARGBHex())) {
					range.add(cell);
				}
			}
		}
		return range;
	}

	public static void main(String[] args) {
		WorkbookComparison wc;
		try {
			wc = new WorkbookComparison("files/Aufgabe_Fahrrad.xlsx");
			wc.startComparison();
			wc.closeWorkbooks();
			System.out.println("Done!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}