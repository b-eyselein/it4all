package model.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class CellStyler {
	
	public static void setCellStyle(Cell cell, IndexedColors color) {
	    CellStyle style = cell.getCellStyle();
	    style.setFillForegroundColor(color.getIndex());
	    style.setFillBackgroundColor(color.getIndex());
	    cell.setCellStyle(style);
	}
	
	public static void setCellComment(Cell cell, String message) {
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
