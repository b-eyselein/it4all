package model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;


public class NoPartyDate {
  
  @Test
  public void testCorrectDTDAgainstXML() {
    File referenceFile = new File("test/resources/partyNoDate.xml");
    File solution = new File("test/resources/partyNoDate.dtd");
    List<XMLError> out = XmlCorrector.correctDTDAgainstXML(solution, referenceFile);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);

    XMLError error = out.get(0);

    assertEquals(XmlErrorType.ERROR, error.getErrorType());
    assertEquals("Attribute \"datum\" must be declared for element type \"party\".", error.getErrorMessage());
  
  }
  
}
