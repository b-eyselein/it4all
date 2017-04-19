package model;

import static org.hamcrest.CoreMatchers.equalTo;
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
public class WrongTagFailTest {
  
  /**
   * Test method for
   * {@link model.CorrectorXml#correctXMLAgainstDTD(java.io.File)}.
   */
  @Test
  public void testCorrectXMLAgainstDTD() {
    Path file = Paths.get("test", "resources", "partyWrongTag.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(file);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(2));
    
    XMLError firstError = out.get(0);
    XMLError secondError = out.get(1);
    
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
    Path xml = Paths.get("test", "resources", "noteWrongTag.xml");
    Path xsd = Paths.get("test", "resources", "note.xsd");
    List<XMLError> out = XmlCorrector.correctXMLAgainstXSD(xml, xsd);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));
    
    XMLError error = out.get(0);
    
    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(5));
    assertThat(error.getErrorMessage(), equalTo(
        "cvc-complex-type.2.4.a: Invalid content was found starting with element 'sender'. One of '{from}' is expected."));
  }
}
