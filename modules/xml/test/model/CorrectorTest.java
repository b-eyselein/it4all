package model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

/**
 * @author rav
 *
 */
public class CorrectorTest {

  /**
   * Test method for
   * {@link model.CorrectorXml#correctDTDAgainstXML(java.io.File)}.
   */
  @Test
  public void testCorrectDTDAgainstXML() {
    File referenceFile = new File("test/resources/party.xml");
    File solution = new File("test/resources/party.dtd");
    List<XMLError> out = XmlCorrector.correctDTDAgainstXML(solution, referenceFile);
    assertTrue(out.isEmpty());
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    File file = new File("test/resources/party.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertTrue(out.isEmpty());
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File xml = new File("test/resources/note.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = null;
    out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertNotNull(out);
    assertTrue(out.isEmpty());
  }

}
