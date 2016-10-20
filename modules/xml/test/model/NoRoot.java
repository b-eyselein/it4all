package model;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.util.List;

import org.junit.Test;

import model.exercise.EvaluationResult;

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
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(2));

    XMLError error = (XMLError) out.get(0);
    XMLError fatalError = (XMLError) out.get(1);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(1));
    assertThat(error.getErrorMessage(), equalTo("Document root element \"gast\", must match DOCTYPE root \"party\"."));

    assertThat(fatalError.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(fatalError.getLine(), equalTo(6));
    assertThat(fatalError.getErrorMessage(),
        equalTo("The markup in the document following the root element must be well-formed."));
  }

  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstXSD(java.io.File, java.io.File)}
   * .
   */
  @Test
  public void testCorrectXMLAgainstXSD() {
    File xml = new File("test/resources/noteNoRoot.xml");
    File xsd = new File("test/resources/note.xsd");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(2));

    XMLError error = (XMLError) out.get(0);
    XMLError fatalError = (XMLError) out.get(1);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(2));
    assertThat(error.getErrorMessage(), equalTo("Cannot find the declaration of element 'to'."));

    assertThat(fatalError.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(fatalError.getLine(), equalTo(3));
    assertThat(fatalError.getErrorMessage(),
        equalTo("The markup in the document following the root element must be well-formed."));
  }

}
