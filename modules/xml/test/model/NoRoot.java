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
public class NoRoot {

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    File file = new File("test/resources/partyNoRoot.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 2);

    XMLError error = out.get(0);
    XMLError fatalError = out.get(1);

    assertEquals(XmlErrorType.ERROR, error.getErrorType());
    assertEquals(3, error.getLine());
    assertEquals("Document root element \"gast\", must match DOCTYPE root \"party\".", error.getErrorMessage());

    assertEquals(XmlErrorType.FATALERROR, fatalError.getErrorType());
    assertEquals(8, fatalError.getLine());
    assertEquals("The markup in the document following the root element must be well-formed.", fatalError.getErrorMessage());
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File file = new File("test/resources/noteNoRoot.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = null;
    out = XmlCorrector.correctXMLAgainstXSD(xsd, file);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 2);

    XMLError error = out.get(0);
    XMLError fatalError = out.get(1);

    assertEquals(XmlErrorType.ERROR, error.getErrorType());
    assertEquals(2, error.getLine());
    assertEquals("cvc-elt.1.a: Cannot find the declaration of element 'to'.", error.getErrorMessage());

    assertEquals(XmlErrorType.FATALERROR, fatalError.getErrorType());
    assertEquals(3, fatalError.getLine());
    assertEquals("The markup in the document following the root element must be well-formed.", fatalError.getErrorMessage());
  }

}
