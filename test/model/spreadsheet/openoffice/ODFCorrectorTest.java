package model.spreadsheet.openoffice;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Table;

public class ODFCorrectorTest {
  
  private ODFCorrector corrector = new ODFCorrector();
  private Path standardDocument = Paths.get("/home/bjorn/Workspace/JAV_it4all/test/resources/standard.ods");
  
  @Test
  public void testGetColoredRange() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testLoadDocument() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    assertNotNull(document);
  }
  
  @Test
  public void testLoadDocumentWithWrongPath() {
    assertNull(corrector.loadDocument(Paths.get("")));
  }
  
  @Test
  public void testGetSheetCount() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    assertThat(corrector.getSheetCount(document), equalTo(1));
    
    fail("NEEDS MORE TESTS!");
  }
  
  @Test
  public void testCompareNumberOfChartsInDocument() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testGetSheetByIndex() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    Table sheet = corrector.getSheetByIndex(document, 0);
    assertNotNull(sheet);
    assertThat(sheet.getOwnerDocument(), equalTo(document));
    
    fail("NEEDS MORE TESTS!");
  }
  
  @Test
  public void testCompareSheet() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testSaveCorrectedSpreadsheet() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testCloseDocument() {
    SpreadsheetDocument document = corrector.loadDocument(standardDocument);
    assertNotNull(document);
    // TODO: komischer Fehler (NullpointerException?)
    corrector.closeDocument(document);
    assertNull(document);
  }
  
}
