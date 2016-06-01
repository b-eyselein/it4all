import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import model.CorrectorXml;
import model.ElementResult;

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
    List<ElementResult> out = CorrectorXml.correctXMLAgainstDTD(xml);
    // TODO: Verwende assertEquals als statischer Import, vergleiche mit
    // assertEquals und verwende Hamcrest-Matcher equalTo, da schönere
    // Fehlermeldung
    assertEquals("TODO: Fehlermeldung wie: Falsche Anzahl an Fehlermeldungen bekommen!", out.size(), 1);
    // Assert.assertTrue(out.size() == 1);
    assertEquals(
        "FATAL ERROR:" + "\n" + "Zeile: 8" + "\n" + "Fehler: "
            + "The element type \"getraenk\" must be terminated by the matching end-tag \"</getraenk>\".\n",
        out.get(0).getMessage());
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
    List<ElementResult> out = null;
    try {
      out = CorrectorXml.correctXMLAgainstXSD(xsd, xml);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO: siehe oben...
    assertEquals(out.size(), 1);
    // assertTrue(out.size() == 1);
    assertEquals(
        "FATAL ERROR:" + "\n" + "Zeile: 7" + "\n" + "Fehler: "
            + "The element type \"to\" must be terminated by the matching end-tag \"</to>\".\n",
        out.get(0).getMessage());
  }
}
