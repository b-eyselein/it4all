package model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import model.result.EvaluationResult;

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
    Path referenceFile = Paths.get("test", "resources", "party.xml");
    List<EvaluationResult> out = XmlCorrector.correctDTDAgainstXML(referenceFile);
    assertTrue(out.isEmpty());
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    Path file = Paths.get("test", "resources", "party.xml");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertTrue(out.isEmpty());
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    Path xml = Paths.get("test", "resources", "note.xml");
    Path xsd = Paths.get("test", "resources", "note.xsd");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertNotNull(out);
    assertTrue(out.isEmpty());
  }

}
