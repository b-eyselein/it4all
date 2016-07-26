package model;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class NoPartyDate {
  
  @Test
  public void testCorrectDTDAgainstXML() {
    File referenceFile = new File("test/resources/partyNoDate.xml");
    File solution = new File("test/resources/partyNoDate.dtd");
    List<XMLError> out = XmlCorrector.correctDTDAgainstXML(solution, referenceFile);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));
    
    XMLError error = out.get(0);
    
    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getErrorMessage(), equalTo("Attribute \"datum\" must be declared for element type \"party\"."));
  }
  
}
