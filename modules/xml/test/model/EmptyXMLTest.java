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
  }

  @Test
  public void xmlNoElement() {
    File xml = new File("test/resources/xmlNoElement.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(xml);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    assertEquals("FATALERROR:\n" + "Fehler: leere XML\n", out.get(0).toString());
  }

}
