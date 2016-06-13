package model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import org.junit.Test;

/**
 *
 */

/**
 * @author rav
 *
 */
public class ClosingTagFailTest {
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   *
   * Failing because of missing closing tag
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    File xml = new File("test/resources/partyNoClosingTag.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(xml);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    // FIXME: vergleiche nicht die toString() sondern jeweils die Setter!
    // Beispiel:
    XMLError onlyError = out.get(0);
    assertEquals(XmlErrorType.FATALERROR, onlyError.errorType);
    assertEquals(8, onlyError.line);
    assertEquals("Fehler: The element type \"getraenk\" must be terminated by the matching end-tag \"</getraenk>\".",
        onlyError.errorMessage);
    // assertEquals(
    // "FATALERROR" + ":\nZeile: " + "8" + "\nFehler: "
    // + "The element type \"getraenk\" must be terminated by the matching
    // end-tag \"</getraenk>\".\n",
    // out.get(0).toString());
  }
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File xml = new File("test/resources/noteNoClosingTag.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = null;
    out = XmlCorrector.correctXMLAgainstXSD(xsd, xml);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    
    XMLError onlyError = out.get(0);
    assertEquals(XmlErrorType.FATALERROR, onlyError.errorType);
    assertEquals(7, onlyError.line);
    assertEquals("Fehler: The element type \"to\" must be terminated by the matching end-tag \"</to>\".\n",
        onlyError.errorMessage);
    
    // assertEquals("FATALERROR:" + "\n" + "Zeile: 7" + "\n" + "Fehler: "
    // + "The element type \"to\" must be terminated by the matching end-tag
    // \"</to>\".\n", out.get(0).toString());
  }
}
