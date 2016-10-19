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
public class WrongTagFailTest {
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    File file = new File("test/resources/partyWrongTag.xml");
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(2));
    
    XMLError firstError = (XMLError) out.get(0);
    XMLError secondError = (XMLError) out.get(1);
    
    assertThat(firstError.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(firstError.getLine(), equalTo(2));
    assertThat(firstError.getErrorMessage(), equalTo("Element type \"guest\" must be declared."));
    
    assertThat(secondError.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(secondError.getLine(), equalTo(13));
    assertThat(secondError.getErrorMessage(), equalTo("The content of element type \"party\" must match \"(gast)*\"."));
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
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));
    
    XMLError error = (XMLError) out.get(0);
    
    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(5));
    assertThat(error.getErrorMessage(), equalTo(
        "cvc-complex-type.2.4.a: Invalid content was found starting with element 'sender'. One of '{from}' is expected."));
  }
}
