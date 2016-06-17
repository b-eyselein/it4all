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

    XMLError error = out.get(0);

    assertEquals(XmlErrorType.ERROR, error.getErrorType());
    assertEquals(9, error.getLine());
    assertEquals("Attribute \"name\" is required and must be specified for element type \"gast\".", error.getErrorMessage());
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File xml = new File("test/resources/noteMissingTag.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = null;
    out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);

    XMLError error = out.get(0);

    assertEquals(XmlErrorType.ERROR, error.getErrorType());
    assertEquals(5, error.getLine());
    assertEquals("cvc-complex-type.2.4.a: Invalid content was found starting with element 'body'. One of '{heading}' is expected.", error.getErrorMessage());
  }

}
