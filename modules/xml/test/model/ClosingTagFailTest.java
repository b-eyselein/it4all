package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    Path xml = Paths.get("test", "resources", "partyNoClosingTag.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(xml);
    
    assertNotNull(out);
    assertThat("Es sollte nur ein Fehler sein, es sind aber " + out.size() + " Fehler!", out.size(), equalTo(1));
    
    XMLError error = out.get(0);
    
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
    Path xml = Paths.get("test", "resources", "noteNoClosingTag.xml");
    Path xsd = Paths.get("test", "resources", "note.xsd");
    List<XMLError> out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    
    assertNotNull(out);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));
    
    XMLError error = out.get(0);
    
    assertThat(error.errorType, equalTo(XmlErrorType.FATALERROR));
    assertThat(error.line, equalTo(7));
    assertThat(error.errorMessage,
        equalTo("The element type \"to\" must be terminated by the matching end-tag \"</to>\"."));
  }
}
