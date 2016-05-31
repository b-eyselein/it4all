package model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */

/**
 * @author rav
 *
 */
public class WrongTagFailTest {
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    File file = new File("test/resources/partyWrongTag.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 2);
    assertEquals("ERROR:" + "\n" + "Zeile: 4" + "\n" + "Fehler: " + "Element type \"guest\" must be declared.\n",
        out.get(0).toString());
    assertEquals("ERROR:" + "\n" + "Zeile: 15" + "\n" + "Fehler: "
        + "The content of element type \"party\" must match \"(gast)*\".\n", out.get(1).toString());
  }
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File file = new File("test/resources/noteWrongTag.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = null;
    try {
      out = XmlCorrector.correctXMLAgainstXSD(xsd, file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    assertEquals(
        "ERROR:" + "\n" + "Zeile: 5" + "\n" + "Fehler: "
            + "cvc-complex-type.2.4.a: Invalid content was found starting with element 'sender'. One of '{from}' is expected.\n",
        out.get(0).toString());
  }
}
