package model;

import static org.junit.Assert.assertNotNull;
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
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstDTD(xml);
    
    assertNotNull(out);
    assertThat("Es sollte nur ein Fehler sein, es sind aber " + out.size() + " Fehler!", out.size(), equalTo(1));
    
    XMLError error = (XMLError) out.get(0);
    
    assertThat(error.errorType, equalTo(XmlErrorType.FATALERROR));
    assertThat(error.line, equalTo(6));
    assertThat(error.errorMessage,
        equalTo("The element type \"getraenk\" must be terminated by the matching end-tag \"</getraenk>\"."));
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
    List<EvaluationResult> out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    
    assertNotNull(out);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));
    
    XMLError error = (XMLError) out.get(0);
    
    assertThat(error.errorType, equalTo(XmlErrorType.FATALERROR));
    assertThat(error.line, equalTo(7));
    assertThat(error.errorMessage,
        equalTo("The element type \"to\" must be terminated by the matching end-tag \"</to>\"."));
  }
}
