import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
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
    List<ElementResult> out = CorrectorXml.correctXMLAgainstDTD(file);
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
    List<ElementResult> out = null;
    try {
      out = CorrectorXml.correctXMLAgainstXSD(xsd, xml);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assert.assertNotNull(out);
    Assert.assertTrue(out.isEmpty());
  }

}
