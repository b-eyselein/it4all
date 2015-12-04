package model.spreadsheet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import model.spreadsheet.XLSXCorrector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.junit.Test;

public class XLSXCorrectorTest {
  
  private XLSXCorrector corrector = new XLSXCorrector();
  private Path standardDocument = Paths.get("test/resources/spreadsheet/standard.xlsx");
  
  private Path schullandheimDir = Paths.get("test/resources/spreadsheet/schullandheim");
  private Path schullandheimMuster = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim_Muster.xlsx");
  private Path schullandheimTeilLoesung = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim.xlsx");
  
  @Test
  public void testCompareCellValues() {
    Workbook muster = corrector.loadDocument(schullandheimMuster);
    Workbook teilLsg = corrector.loadDocument(schullandheimTeilLoesung);
    
    XSSFCell musterCell = (XSSFCell) muster.getSheetAt(2).getRow(15).getCell(7);
    XSSFCell compareCell = (XSSFCell) teilLsg.getSheetAt(2).getRow(15).getCell(7);
    assertThat(corrector.compareCellValues(musterCell, compareCell), equalTo("Wert richtig."));
    
    // Wert in Muster, Compare leer
    musterCell = (XSSFCell) muster.getSheetAt(3).getRow(16).getCell(5);
    compareCell = (XSSFCell) teilLsg.getSheetAt(3).getRow(16).getCell(5);
    assertThat(corrector.compareCellValues(musterCell, compareCell), equalTo("Keinen Wert angegeben!"));
    
    // Wert in Muster, Compare falsch
    musterCell = (XSSFCell) muster.getSheetAt(3).getRow(19).getCell(5);
    compareCell = (XSSFCell) teilLsg.getSheetAt(3).getRow(19).getCell(5);
    // TODO: Zahl schoener formatieren... 12.142857142857142
    assertThat(corrector.compareCellValues(musterCell, compareCell),
        equalTo("Wert falsch. Erwartet wurde '12.142857142857142'."));
  }
  
  @Test
  public void testCompareCellFormulas() {
    Workbook muster = corrector.loadDocument(schullandheimMuster);
    Workbook teilLsg = corrector.loadDocument(schullandheimTeilLoesung);
    
    XSSFCell musterCell = (XSSFCell) muster.getSheetAt(2).getRow(15).getCell(7);
    XSSFCell compareCell = (XSSFCell) teilLsg.getSheetAt(2).getRow(15).getCell(7);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell), equalTo("Formel richtig."));
    
    // Kein Formel in Muster, Compare leer
    musterCell = (XSSFCell) muster.getSheetAt(3).getRow(16).getCell(1);
    compareCell = (XSSFCell) teilLsg.getSheetAt(3).getRow(16).getCell(1);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell), equalTo("Es war keine Formel anzugeben."));
    
    // Wert in Muster, Compare leer
    musterCell = (XSSFCell) muster.getSheetAt(3).getRow(16).getCell(5);
    compareCell = (XSSFCell) teilLsg.getSheetAt(3).getRow(16).getCell(5);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell), equalTo("Keine Formel angegeben!"));
    
    // Wert in Muster, Compare leer
    musterCell = (XSSFCell) muster.getSheetAt(3).getRow(19).getCell(5);
    compareCell = (XSSFCell) teilLsg.getSheetAt(3).getRow(19).getCell(5);
    assertThat(corrector.compareCellFormulas(musterCell, compareCell), equalTo("Formel falsch. Der Bereich D20 fehlt."));
  }
  
  @Test
  public void testGetColoredRange() {
    Workbook muster = corrector.loadDocument(schullandheimMuster);
    List<XSSFCell> coloredRange = corrector.getColoredRange(muster.getSheetAt(1));
    assertTrue(coloredRange.isEmpty());
    
    coloredRange = corrector.getColoredRange(muster.getSheetAt(2));
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
  public void testLoadDocument() {
    Workbook document = null;
    
    document = corrector.loadDocument(standardDocument);
    assertNotNull("Dokument standardDokument konnte nicht geladen werden!", document);
    
    document = corrector.loadDocument(schullandheimMuster);
    assertNotNull("Dokument schullandheimMuster konnte nicht geladen werden!", document);
    
    document = corrector.loadDocument(schullandheimTeilLoesung);
    assertNotNull("Dokument schullandheimMuster konnte nicht geladen werden!", document);
  }
  
  @Test
  public void testLoadDocumentWithWrongPath() {
    assertNull(corrector.loadDocument(Paths.get("")));
  }
  
  @Test
  public void testGetSheetCount() {
    Workbook document = null;
    
    document = corrector.loadDocument(standardDocument);
    assertNotNull("Standarddokument konnte nicht geladen werden!", document);
    assertThat(corrector.getSheetCount(document), equalTo(1));
    
    document = corrector.loadDocument(schullandheimMuster);
    assertThat(corrector.getSheetCount(document), equalTo(8));
    
    document = corrector.loadDocument(schullandheimTeilLoesung);
    assertThat(corrector.getSheetCount(document), equalTo(8));
  }
  
  @Test
  public void testCompareNumberOfChartsInDocument() {
    Workbook document = corrector.loadDocument(standardDocument);
    assertNull(corrector.compareNumberOfChartsInDocument(document, document));
  }
  
  @Test
  public void testGetSheetByIndex() {
    Workbook document = corrector.loadDocument(standardDocument);
    assertNotNull("Standarddokument konnte nicht geladen werden!", document);
    Sheet sheet = corrector.getSheetByIndex(document, 0);
    assertNotNull(sheet);
    
    Workbook muster = corrector.loadDocument(schullandheimMuster);
    Sheet musterSheet = corrector.getSheetByIndex(muster, 0);
    assertNotNull(musterSheet);
    assertThat(musterSheet.getRow(0).getCell(0).toString(), equalTo("Verwalten und Auswerten von Daten"));
    musterSheet = corrector.getSheetByIndex(muster, 4);
    assertNotNull(musterSheet);
    assertThat(musterSheet.getRow(15).getCell(10).toString(), equalTo("5% Rabatt auf den Gesamtpreis"));
  }
  
  @Test
  public void testCompareSheet() {
    // FIXME: implement and test!
    // fail("Not yet implemented");
  }
  
  @Test
  public void testSaveCorrectedSpreadsheet() {
    // FIXME: implement and test!
    // fail("Not yet implemented");
  }
  
  @Test
  public void testCloseDocument() {
    Workbook document = corrector.loadDocument(schullandheimMuster);
    assertNotNull(document);
    corrector.closeDocument(document);
  }
  
  @Test
  public void testGetCellByPosition() {
    Workbook muster = corrector.loadDocument(schullandheimMuster);
    Sheet musterSheet = corrector.getSheetByIndex(muster, 0);
    assertThat(corrector.getCellByPosition(musterSheet, 0, 0).toString(), equalTo(musterSheet.getRow(0).getCell(0)
        .toString()));
    
    assertNull(corrector.getCellByPosition(musterSheet, 7, 5));
    
    Sheet musterSheet4 = corrector.getSheetByIndex(muster, 4);
    assertThat(corrector.getCellByPosition(musterSheet4, 15, 10).toString(), equalTo(musterSheet4.getRow(15)
        .getCell(10).toString()));
    
  }
  
  @Test
  public void testSetCellComment() {
    String message = "Dies ist eine Testnachricht!";
    String message2 = "Dies ist eine zweite  Testnachricht!";
    
    Workbook document = corrector.loadDocument(standardDocument);
    XSSFCell cell = (XSSFCell) document.getSheetAt(0).getRow(0).getCell(0);
    
    assertNull(cell.getCellComment());
    
    corrector.setCellComment(cell, null);
    assertNull(cell.getCellComment());
    
    corrector.setCellComment(cell, "");
    assertNull(cell.getCellComment());
    
    corrector.setCellComment(cell, message);
    assertThat(cell.getCellComment().getString().getString(), equalTo(message));
    
    corrector.setCellComment(cell, message2);
    assertThat(cell.getCellComment().getString().getString(), equalTo(message2));
    
    cell.removeCellComment();
  }
  
  @Test
  public void testCompareChartsInSheet() {
    
    Workbook muster = corrector.loadDocument(schullandheimMuster);
    assertThat(corrector.compareChartsInSheet(muster.getSheetAt(0), muster.getSheetAt(0)),
        equalTo("Es waren keine Diagramme zu erstellen."));
    
    Workbook solution = corrector.loadDocument(schullandheimTeilLoesung);
    assertThat(corrector.compareChartsInSheet(solution.getSheetAt(0), muster.getSheetAt(2)),
        equalTo("Falsche Anzahl an Diagrammen im Sheet (Erwartet: 2, Gefunden: 0)."));
    
    assertThat(corrector.compareChartsInSheet(muster.getSheetAt(2), muster.getSheetAt(2)),
        equalTo("Diagramm(e) richtig."));
    // FIXME: Implement!
    // fail("Still things to implement!");
  }
}
