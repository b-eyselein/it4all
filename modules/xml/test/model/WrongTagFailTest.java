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

    XMLError firstError = out.get(0);
    XMLError secondError = out.get(1);

    assertEquals(XmlErrorType.ERROR, firstError.getErrorType());
    assertEquals(4, firstError.getLine());
    assertEquals("Element type \"guest\" must be declared.", firstError.getErrorMessage());

    assertEquals(XmlErrorType.ERROR, secondError.getErrorType());
    assertEquals(15, secondError.getLine());
    assertEquals("The content of element type \"party\" must match \"(gast)*\".", secondError.getErrorMessage());
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File xml = new File("test/resources/noteWrongTag.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = null;
    out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);

    XMLError error = out.get(0);

    assertEquals(XmlErrorType.ERROR, error.getErrorType());
    assertEquals(5, error.getLine());
    assertEquals("cvc-complex-type.2.4.a: Invalid content was found starting with element 'sender'. One of '{from}' is expected.", error.getErrorMessage());
  }
}
