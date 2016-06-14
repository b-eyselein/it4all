package model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class EmptyXMLTest {
  
  @Test
  public void emptyXmlAgainstXSD() {
    File emptyXML = new File("test/resources/empty.xml");
    File xsd = new File("test/resources/note.xsd");
    List<XMLError> out = XmlCorrector.correctXMLAgainstXSD(emptyXML, xsd);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    
    XMLError error = out.get(0);
    
    assertEquals(XmlErrorType.FATALERROR, error.getErrorType());
    assertEquals("Ihre Eingabedaten konnten nicht geladen werden!", error.getErrorMessage());
  }
  
  @Test
  public void xmlNoElement() {
    File xml = new File("test/resources/xmlNoElement.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(xml);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    
    XMLError error = out.get(0);
    
    assertEquals(XmlErrorType.FATALERROR, error.getErrorType());
    assertEquals("leere XML", error.getErrorMessage());
  }
  
}
