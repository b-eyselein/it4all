package model.spreadsheet;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;
import org.odftoolkit.simple.table.Table;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class SheetStyler {
	
	public static void setODFSheetComment(Table table, String message, int row, int column) {
		if (!message.equals("")) {
			org.odftoolkit.simple.table.Cell cell = table.getCellByPosition(column, row);
			// Remove note if exists
			if (cell.getNoteText() != null) {
				cell.setNoteText(null);
			}
		    // Set note
			cell.setNoteText(message);
		}
	}

	public static void setXLSSheetComment(Sheet sheet, String message, int row, int column) {
		if (!message.equals("")) {
			org.apache.poi.ss.usermodel.Cell cell = sheet.getRow(row).getCell(column);
			// Remove comment if exists
			if (cell.getCellComment() != null) {
				cell.removeCellComment();
			}
			// Remove comment if exists
			if (cell.getCellComment() != null) {
				cell.removeCellComment();
			}
			// Create new drawing object
			Drawing drawing = sheet.createDrawingPatriarch();
			CreationHelper factory = sheet.getWorkbook().getCreationHelper();
			// Create comment space
			ClientAnchor anchor = factory.createClientAnchor();
		    anchor.setCol1(cell.getColumnIndex() + 1);
		    anchor.setCol2(cell.getColumnIndex() + 3);
		    anchor.setRow1(cell.getRowIndex() + 1);
		    anchor.setRow2(cell.getRowIndex() + 3);
		    // Insert new comment
		    Comment comment = drawing.createCellComment(anchor);
		    RichTextString str = factory.createRichTextString(message);
		    comment.setVisible(true);
		    comment.setString(str);
		    // Set comment
		    cell.setCellComment(comment);
		}
	}

}
