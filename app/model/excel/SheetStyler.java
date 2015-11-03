package model.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class SheetStyler {

	public static void setSheetComment(Sheet sheet, String message, int row, int column) {
		Cell cell = sheet.getRow(row).getCell(column);
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
