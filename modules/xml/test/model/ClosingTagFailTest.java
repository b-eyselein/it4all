package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

    assertNotNull(out);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);

    XMLError error = out.get(0);

    assertEquals(XmlErrorType.FATALERROR, error.getErrorType());
    assertEquals(8, error.getLine());
    assertEquals("The element type \"getraenk\" must be terminated by the matching end-tag \"</getraenk>\".",
        error.getErrorMessage());
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
    out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);

    assertNotNull(out);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);

    XMLError error = out.get(0);

    assertEquals(XmlErrorType.FATALERROR, error.errorType);
    assertEquals(7, error.line);
    assertEquals("The element type \"to\" must be terminated by the matching end-tag \"</to>\".",
        error.errorMessage);
  }
}
