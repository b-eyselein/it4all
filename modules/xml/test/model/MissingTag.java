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
public class MissingTag {
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    File file = new File("test/resources/partyMissingAttribute.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    assertEquals(
        "ERROR:" + "\n" + "Zeile: 9" + "\n" + "Fehler: "
            + "Attribute \"name\" is required and must be specified for element type \"gast\".\n",
        out.get(0).toString());
  }
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File file = new File("test/resources/noteMissingTag.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = null;
    out = XmlCorrector.correctXMLAgainstXSD(xsd, file);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    assertEquals(
        "ERROR:" + "\n" + "Zeile: 5" + "\n" + "Fehler: "
            + "cvc-complex-type.2.4.a: Invalid content was found starting with element 'body'. One of '{heading}' is expected.\n",
        out.get(0).toString());
  }
  
}
