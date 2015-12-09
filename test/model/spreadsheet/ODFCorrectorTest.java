package model.spreadsheet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

public class ODFCorrectorTest {
  
  private ODFCorrector corrector = new ODFCorrector();
  private Path standardDocument = Paths.get("test/resources/spreadsheet/standard.ods");
  
  private Path schullandheimDir = Paths.get("test/resources/spreadsheet/schullandheim");
  private Path schullandheimMuster = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim_Muster.ods");
  private Path schullandheimTeilLoesung = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim.ods");
  
  @Test
  public void compareChartsInSheet() {
    assertNull(corrector.compareChartsInSheet(null, null));
  }
  
  @Test
  public void testCloseDocument() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    assertNotNull(document);
    corrector.closeDocument(document);
  }
  
  @Test
  public void testCompareCellFormulas() {
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    SpreadsheetDocument teilLsg = corrector.loadDocument(schullandheimTeilLoesung);
    
    Cell musterCell = muster.getSheetByIndex(2).getCellByPosition(7, 15);
    Cell compareCell = teilLsg.getSheetByIndex(2).getCellByPosition(7, 15);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell), equalTo("Formel richtig."));
    
    // Wert in Muster null, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(3, 9);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(3, 9);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell), equalTo(""));
    
    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 16);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 16);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell), equalTo("Keine Formel angegeben!"));
    
    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 19);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 19);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell),
        equalTo("Formel falsch. Der Bereich [D20] fehlt."));
  }
  
  @Test
  public void testCompareCellValues() {
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    SpreadsheetDocument teilLsg = corrector.loadDocument(schullandheimTeilLoesung);
    
    Cell musterCell = muster.getSheetByIndex(2).getCellByPosition(7, 15);
    Cell compareCell = teilLsg.getSheetByIndex(2).getCellByPosition(7, 15);
    assertThat(corrector.compareCellValues(musterCell, compareCell), equalTo("Wert richtig."));
    
    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 16);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 16);
    assertThat(corrector.compareCellValues(musterCell, compareCell), equalTo("Keinen Wert angegeben!"));
    
    // Wert in Muster, Compare falsch
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 19);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 19);
    assertThat(corrector.compareCellValues(musterCell, compareCell), equalTo("Wert falsch. Erwartet wurde '12,14 €'."));
    
  }
  
  @Test
  public void testCompareNumberOfChartsInDocument() {
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    assertThat(corrector.compareNumberOfChartsInDocument(muster, muster),
        equalTo("Richtige Anzahl Diagramme gefunden."));
    
    SpreadsheetDocument solution = corrector.loadDocument(schullandheimTeilLoesung);
    assertThat(corrector.compareNumberOfChartsInDocument(solution, muster),
        equalTo("Falsche Anzahl Diagramme im Dokument (erwartet: 2, gezählt: 0)."));
    
    assertThat(corrector.compareNumberOfChartsInDocument(solution, solution),
        equalTo("Es waren keine Diagramme zu erstellen."));
    
    muster.close();
    solution.close();
  }
  
  @Test
  public void testCompareSheet() {
    // FIXME: implement and test!
    // fail("Not yet implemented");
  }
  
  @Test
  public void testGetCellByPosition() {
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    Table musterSheet = corrector.getSheetByIndex(muster, 0);
    assertThat(corrector.getCellByPosition(musterSheet, 0, 0).getStringValue(),
        equalTo(musterSheet.getCellByPosition("A1").getStringValue()));
    assertThat(corrector.getCellByPosition(musterSheet, 7, 5).getStringValue(),
        equalTo(musterSheet.getCellByPosition("F8").getStringValue()));
    
    Table musterSheet4 = corrector.getSheetByIndex(muster, 4);
    assertThat(corrector.getCellByPosition(musterSheet4, 10, 15).getStringValue(), equalTo(musterSheet4
        .getCellByPosition(10, 15).getStringValue()));
    muster.close();
  }
  
  @Test
  public void testGetColoredRange() {
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    List<Cell> coloredRange = corrector.getColoredRange(muster.getSheetByIndex(1));
    assertTrue(coloredRange.isEmpty());
    
    coloredRange = corrector.getColoredRange(muster.getSheetByIndex(2));
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
  
  @Test
  public void testGetSheetByIndex() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    Table sheet = corrector.getSheetByIndex(document, 0);
    assertNotNull(sheet);
    assertThat(sheet.getOwnerDocument(), equalTo(document));
    document.close();
    
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    Table musterSheet = corrector.getSheetByIndex(muster, 0);
    assertNotNull(musterSheet);
    assertThat(musterSheet.getOwnerDocument(), equalTo(muster));
    assertThat(musterSheet.getCellByPosition("A1").getStringValue(), equalTo("Verwalten und Auswerten von Daten"));
    musterSheet = corrector.getSheetByIndex(muster, 4);
    assertNotNull(musterSheet);
    assertThat(musterSheet.getOwnerDocument(), equalTo(muster));
    assertThat(musterSheet.getCellByPosition("K16").getStringValue(), equalTo("5% Rabatt auf den Gesamtpreis"));
    muster.close();
  }
  
  @Test
  public void testGetSheetCount() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    assertThat(corrector.getSheetCount(document), equalTo(1));
    document.close();
    
    document = corrector.loadDocument(schullandheimMuster);
    assertThat(corrector.getSheetCount(document), equalTo(8));
    document.close();
    
    document = corrector.loadDocument(schullandheimTeilLoesung);
    assertThat(corrector.getSheetCount(document), equalTo(8));
    document.close();
  }
  
  @Test
  public void testLoadDocument() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    assertNotNull(document);
    
    document = corrector.loadDocument(schullandheimMuster);
    assertNotNull(document);
  }
  
  @Test
  public void testLoadDocumentWithWrongPath() {
    assertNull(corrector.loadDocument(Paths.get("")));
  }
  
  @Test
  public void testSaveCorrectedSpreadsheet() {
    // FIXME: implement and test!
    // fail("Not yet implemented");
  }
  
  @Test
  public void testSetCellComment() {
    String message = "Dies ist eine Testnachricht!";
    String message2 = "Dies ist eine zweite  Testnachricht!";
    
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    Cell cell = document.getSheetByIndex(0).getCellByPosition("D4");
    
    assertNull(cell.getNoteText());
    
    corrector.setCellComment(cell, null);
    assertNull(cell.getNoteText());
    
    corrector.setCellComment(cell, "");
    assertNull(cell.getNoteText());
    
    corrector.setCellComment(cell, message);
    assertThat(cell.getNoteText(), equalTo(message));
    
    corrector.setCellComment(cell, message2);
    assertThat(cell.getNoteText(), equalTo(message2));
    
    cell.setNoteText(null);
    
  }
  
  @Test
  public void testSetCellStyle() {
    Cell cell = corrector.loadDocument(standardDocument).getSheetByIndex(0).getCellByPosition("B2");
    Font font = new Font("Arial", FontStyle.BOLD, 11);
    assertNotNull(cell.getFont());
    corrector.setCellStyle(cell, font, Color.AQUA);
    assertThat(cell.getFont(), equalTo(font));
    cell.setCellStyleName(null);
  }
  
}
