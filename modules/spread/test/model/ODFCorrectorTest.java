package model;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class ODFCorrectorTest {

  public SpreadsheetDocument loadDocument(Path p) {
    return ODFCorrector$.MODULE$.loadDocument(p).get();
  }

  private Path standardDocument = Paths.get("test/resources/spreadsheet/standard.ods");

  private Path schullandheimDir = Paths.get("test/resources/spreadsheet/schullandheim");
  private Path schullandheimMuster = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim_Muster.ods");
  private Path schullandheimTeilLoesung = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim.ods");

  @Test public void compareChartsInSheet() {
    assertNull(ODFCorrector$.MODULE$.compareChartsInSheet(null, null));
  }

  @Test public void testCloseDocument() throws CorrectionException {
    SpreadsheetDocument document = loadDocument(standardDocument);
    assertNotNull(document);
    ODFCorrector$.MODULE$.closeDocument(document);
  }

  @Test public void testCompareCellFormulas() throws CorrectionException {
    SpreadsheetDocument muster = loadDocument(schullandheimMuster);
    SpreadsheetDocument teilLsg = loadDocument(schullandheimTeilLoesung);

    Cell musterCell = muster.getSheetByIndex(2).getCellByPosition(7, 15);
    Cell compareCell = teilLsg.getSheetByIndex(2).getCellByPosition(7, 15);
    assertThat(ODFCorrector$.MODULE$.compareCellFormulas(musterCell, compareCell),
        equalTo(StringConsts.COMMENT_FORMULA_CORRECT));

    // Wert in Muster null, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(3, 9);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(3, 9);
    assertThat(ODFCorrector$.MODULE$.compareCellFormulas(musterCell, compareCell), equalTo(""));

    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 16);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 16);
    assertThat(ODFCorrector$.MODULE$.compareCellFormulas(musterCell, compareCell),
        equalTo(StringConsts.COMMENT_FORMULA_MISSING));

    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 19);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 19);
    assertThat(ODFCorrector$.MODULE$.compareCellFormulas(musterCell, compareCell), equalTo(String
        .format(StringConsts.COMMENT_FORMULA_INCORRECT_VAR,
            String.format(StringConsts.COMMENT_RANGE_MISSING_VAR, "[D20]"))));
  }

  @Test public void testCompareCellValues() throws CorrectionException {
    SpreadsheetDocument muster = loadDocument(schullandheimMuster);
    SpreadsheetDocument teilLsg = loadDocument(schullandheimTeilLoesung);

    Cell musterCell = muster.getSheetByIndex(2).getCellByPosition(7, 15);
    Cell compareCell = teilLsg.getSheetByIndex(2).getCellByPosition(7, 15);
    assertThat(ODFCorrector$.MODULE$.compareCellValues(musterCell, compareCell),
        equalTo(StringConsts.COMMENT_VALUE_CORRECT));

    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 16);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 16);
    assertThat(ODFCorrector$.MODULE$.compareCellValues(musterCell, compareCell),
        equalTo(StringConsts.COMMENT_VALUE_MISSING));

    // Wert in Muster, Compare falsch
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 19);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 19);
    assertThat(ODFCorrector$.MODULE$.compareCellValues(musterCell, compareCell),
        equalTo(String.format(StringConsts.COMMENT_VALUE_INCORRECT_VAR, "12,14 €")));

  }

  @Test public void testCompareNumberOfChartsInDocument() throws CorrectionException {
    SpreadsheetDocument muster = loadDocument(schullandheimMuster);
    assertThat(ODFCorrector$.MODULE$.compareNumberOfChartsInDocument(muster, muster),
        equalTo(StringConsts.COMMENT_CHART_NUM_CORRECT));

    SpreadsheetDocument solution = loadDocument(schullandheimTeilLoesung);
    assertThat(ODFCorrector$.MODULE$.compareNumberOfChartsInDocument(solution, muster),
        equalTo(String.format(StringConsts.COMMENT_CHART_NUM_INCORRECT_VAR, 2, 0)));

    assertThat(ODFCorrector$.MODULE$.compareNumberOfChartsInDocument(solution, solution),
        equalTo(StringConsts.COMMENT_CHART_FALSE));

    muster.close();
    solution.close();
  }

  @Test public void testCompareSheet() {
    // TODO: implement and test!
    // fail("Not yet implemented");
  }

  @Test public void testGetCellByPosition() throws CorrectionException {
    SpreadsheetDocument muster = loadDocument(schullandheimMuster);
    Table musterSheet = ODFCorrector$.MODULE$.getSheetByIndex(muster, 0);
    assertThat(ODFCorrector$.MODULE$.getCellByPosition(musterSheet, 0, 0).getStringValue(),
        equalTo(musterSheet.getCellByPosition("A1").getStringValue()));
    assertThat(ODFCorrector$.MODULE$.getCellByPosition(musterSheet, 7, 5).getStringValue(),
        equalTo(musterSheet.getCellByPosition("F8").getStringValue()));

    Table musterSheet4 = ODFCorrector$.MODULE$.getSheetByIndex(muster, 4);
    assertThat(ODFCorrector$.MODULE$.getCellByPosition(musterSheet4, 10, 15).getStringValue(),
        equalTo(musterSheet4.getCellByPosition(10, 15).getStringValue()));
    muster.close();
  }

  @Test public void testGetColoredRange() throws CorrectionException {
    SpreadsheetDocument muster = loadDocument(schullandheimMuster);
    List<Cell> coloredRange = ODFCorrector$.MODULE$.getColoredRange(muster.getSheetByIndex(1));
    assertTrue(coloredRange.isEmpty());

    coloredRange = ODFCorrector$.MODULE$.getColoredRange(muster.getSheetByIndex(2));
    assertThat(coloredRange.size(), equalTo(28));
    for(int i = 0; i < 25; i++) {
      Cell cell = coloredRange.get(i);
      assertThat(cell.getRowIndex(), equalTo(15 + i));
      assertThat(cell.getColumnIndex(), equalTo(7));
    }
    Cell maennlich = coloredRange.get(26);
    assertThat(maennlich.getRowIndex(), equalTo(44));
    assertThat(maennlich.getColumnIndex(), equalTo(1));
    Cell weiblich = coloredRange.get(27);
    assertThat(weiblich.getRowIndex(), equalTo(45));
    assertThat(weiblich.getColumnIndex(), equalTo(1));
  }

  @Test public void testGetSheetByIndex() throws CorrectionException {
    SpreadsheetDocument document = loadDocument(standardDocument);
    Table sheet = ODFCorrector$.MODULE$.getSheetByIndex(document, 0);
    assertNotNull(sheet);
    assertThat(sheet.getOwnerDocument(), equalTo(document));
    document.close();

    SpreadsheetDocument muster = loadDocument(schullandheimMuster);
    Table musterSheet = ODFCorrector$.MODULE$.getSheetByIndex(muster, 0);
    assertNotNull(musterSheet);
    assertThat(musterSheet.getOwnerDocument(), equalTo(muster));
    assertThat(musterSheet.getCellByPosition("A1").getStringValue(), equalTo("Verwalten und Auswerten von Daten"));
    musterSheet = ODFCorrector$.MODULE$.getSheetByIndex(muster, 4);
    assertNotNull(musterSheet);
    assertThat(musterSheet.getOwnerDocument(), equalTo(muster));
    assertThat(musterSheet.getCellByPosition("K16").getStringValue(), equalTo("5% Rabatt auf den Gesamtpreis"));
    muster.close();
  }

  @Test public void testGetSheetCount() throws CorrectionException {
    SpreadsheetDocument document = loadDocument(standardDocument);
    assertThat(ODFCorrector$.MODULE$.getSheetCount(document), equalTo(1));
    document.close();

    document = loadDocument(schullandheimMuster);
    assertThat(ODFCorrector$.MODULE$.getSheetCount(document), equalTo(8));
    document.close();

    document = loadDocument(schullandheimTeilLoesung);
    assertThat(ODFCorrector$.MODULE$.getSheetCount(document), equalTo(8));
    document.close();
  }

  @Test public void testLoadDocument() throws CorrectionException {
    SpreadsheetDocument document = loadDocument(standardDocument);
    assertNotNull(document);

    document = loadDocument(schullandheimMuster);
    assertNotNull(document);
  }

  @Test(expected = CorrectionException.class) public void testLoadDocumentWithWrongPath() throws CorrectionException {
    loadDocument(Paths.get(""));
  }

  @Test public void testSaveCorrectedSpreadsheet() {
    // TODO: implement and test!
    // fail("Not yet implemented");
  }

  @Test public void testSetCellComment() throws CorrectionException {
    String message = "Dies ist eine Testnachricht!";
    String message2 = "Dies ist eine zweite  Testnachricht!";

    SpreadsheetDocument document = loadDocument(standardDocument);
    Cell cell = document.getSheetByIndex(0).getCellByPosition("D4");

    assertNull(cell.getNoteText());

    ODFCorrector$.MODULE$.setCellComment(cell, null);
    assertNull(cell.getNoteText());

    ODFCorrector$.MODULE$.setCellComment(cell, "");
    assertNull(cell.getNoteText());

    ODFCorrector$.MODULE$.setCellComment(cell, message);
    assertThat(cell.getNoteText(), equalTo(message));

    ODFCorrector$.MODULE$.setCellComment(cell, message2);
    assertThat(cell.getNoteText(), equalTo(message2));

    cell.setNoteText(null);

  }

  @Test public void testSetCellStyle() throws CorrectionException {
    Cell cell = loadDocument(standardDocument).getSheetByIndex(0).getCellByPosition("B2");
    Font font = new Font("Arial", FontStyle.BOLD, 11);
    assertNotNull(cell.getFont());
    ODFCorrector$.MODULE$.setCellStyle(cell, font, Color.AQUA);
    assertThat(cell.getFont(), equalTo(font));
    cell.setCellStyleName(null);
  }

}
