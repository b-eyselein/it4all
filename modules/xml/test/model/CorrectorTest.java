package model;

import java.io.File;
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
public class CorrectorTest {
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctDTDAgainstXML(java.io.File)}.
   */
  @Test
  public void testCorrectDTDAgainstXML() {
    // FIXME: implement!
    // fail("Not yet implemented");
  }
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    File file = new File("test/resources/party.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(file);
    Assert.assertTrue(out.isEmpty());
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
    out = XmlCorrector.correctXMLAgainstXSD(xsd, xml);
    Assert.assertNotNull(out);
    Assert.assertTrue(out.isEmpty());
  }
  
}
