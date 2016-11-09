package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import model.result.EvaluationResult;

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
    Path file = Paths.get("test", "resources", "partyMissingAttribute.xml");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XMLError error = (XMLError) out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(7));
    assertThat(error.getErrorMessage(),
        equalTo("Attribute \"name\" is required and must be specified for element type \"gast\"."));
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    Path xml = Paths.get("test", "resources", "noteMissingTag.xml");
    Path xsd = Paths.get("test", "resources", "note.xsd");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XMLError error = (XMLError) out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(5));
    assertThat(error.getErrorMessage(),
        equalTo("Invalid content was found starting with element 'body'. One of '{heading}' is expected."));
  }

}
