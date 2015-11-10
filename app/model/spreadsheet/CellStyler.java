package model.spreadsheet;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class CellStyler {
	
	public static void setODFCellComment(org.odftoolkit.simple.table.Cell cell, String message) {
		if (!message.equals("")) {
			// Remove note if exists
			if (cell.getNoteText() != null) {
				cell.setNoteText(null);
			}
		    // Set note
			cell.setNoteText(message);
		}
	}
	
	public static void setODFCellStyle(org.odftoolkit.simple.table.Cell cell, boolean bool) {
		org.odftoolkit.simple.style.Font fontGreen =
				new org.odftoolkit.simple.style.Font("Arial", FontStyle.BOLD, 11, Color.GREEN);
		org.odftoolkit.simple.style.Font fontRed =
				new org.odftoolkit.simple.style.Font("Arial", FontStyle.ITALIC, 11, Color.RED);
	    if (bool) {
		    cell.setFont(fontGreen);
		} else {
		    cell.setFont(fontRed);
		}
	}
	
	public static void setXLSCellComment(org.apache.poi.ss.usermodel.Cell cell, String message) {
		if (!message.equals("")) {
			// Remove comment if exists
			if (cell.getCellComment() != null) {
				cell.removeCellComment();
			}
			// Create new drawing object
			Drawing drawing = cell.getSheet().createDrawingPatriarch();
			CreationHelper factory = cell.getSheet().getWorkbook().getCreationHelper();
			// Create comment space
			ClientAnchor anchor = factory.createClientAnchor();
		    anchor.setCol1(cell.getColumnIndex());
		    anchor.setCol2(cell.getColumnIndex() + 3);
		    anchor.setRow1(cell.getRowIndex());
		    anchor.setRow2(cell.getRowIndex() + 3);
		    // Insert new comment
		    Comment comment = drawing.createCellComment(anchor);
		    RichTextString str = factory.createRichTextString(message);
		    comment.setVisible(false);
		    comment.setString(str);
		    // Set comment
		    cell.setCellComment(comment);
		}
	}
	
	public static void setXLSCellStyle(org.apache.poi.ss.usermodel.Cell cell, boolean bool) {
	    CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
	    short alignment = cell.getCellStyle().getAlignment();
	    short format = cell.getCellStyle().getDataFormat();
	    org.apache.poi.ss.usermodel.Font font = cell.getSheet().getWorkbook().createFont();
	    if (bool) {
		    font.setBold(true);
		    font.setColor(IndexedColors.GREEN.getIndex());
		} else {
		    font.setItalic(true);
		    font.setColor(IndexedColors.RED.getIndex());
		}
	    style.setFont(font);
	    style.setAlignment(alignment);
	    style.setDataFormat(format);
	    style.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);
	    style.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
	    style.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
	    style.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
	    cell.setCellStyle(style);
	}

}
