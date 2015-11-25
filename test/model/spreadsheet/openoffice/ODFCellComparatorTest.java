package model.spreadsheet.openoffice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;

public class ODFCellComparatorTest {
  
  private ODFCorrector corrector = new ODFCorrector();
  
  private Path schullandheimDir = Paths.get("test/resources/spreadsheet/schullandheim");
  private Path schullandheimMuster = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim_Muster.ods");
  private Path schullandheimTeilLoesung = Paths.get(schullandheimDir.toString(), "Aufgabe_Schullandheim.ods");
  
  @Test
  public void testCompareCellValues() {
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    SpreadsheetDocument teilLsg = corrector.loadDocument(schullandheimTeilLoesung);
    
    Cell musterCell = muster.getSheetByIndex(2).getCellByPosition(7, 15);
    Cell compareCell = teilLsg.getSheetByIndex(2).getCellByPosition(7, 15);
    assertThat(ODFCellComparator.compareCellValues(musterCell, compareCell), equalTo("Wert richtig."));
    
    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 16);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 16);
    assertThat(ODFCellComparator.compareCellValues(musterCell, compareCell), equalTo("Keinen Wert angegeben!"));
    
    // Wert in Muster, Compare falsch
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 19);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 19);
    assertThat(ODFCellComparator.compareCellValues(musterCell, compareCell),
        equalTo("Wert falsch. Erwartet wurde '12,14 €'."));
    
  }
  
  @Test
  public void testCompareCellFormulas() {
    SpreadsheetDocument muster = corrector.loadDocument(schullandheimMuster);
    SpreadsheetDocument teilLsg = corrector.loadDocument(schullandheimTeilLoesung);
    
    Cell musterCell = muster.getSheetByIndex(2).getCellByPosition(7, 15);
    Cell compareCell = teilLsg.getSheetByIndex(2).getCellByPosition(7, 15);
    assertThat(ODFCellComparator.compareCellFormulas(musterCell, compareCell), equalTo("Formel richtig."));
    
    // Wert in Muster null, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(3, 9);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(3, 9);
    assertThat(ODFCellComparator.compareCellFormulas(musterCell, compareCell), equalTo(""));
    
    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 16);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 16);
    assertThat(ODFCellComparator.compareCellFormulas(musterCell, compareCell), equalTo("Keine Formel angegeben!"));
    
    // Wert in Muster, Compare leer
    musterCell = muster.getSheetByIndex(3).getCellByPosition(5, 19);
    compareCell = teilLsg.getSheetByIndex(3).getCellByPosition(5, 19);
    assertThat(ODFCellComparator.compareCellFormulas(musterCell, compareCell),
        equalTo("Formel falsch. Der Bereich D20 fehlt."));
  }
  
}
