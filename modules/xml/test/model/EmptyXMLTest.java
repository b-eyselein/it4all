package model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class EmptyXMLTest {
  
  @Test
  public void emptyXML() {
    File xml = new File("test/resources/empty.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(xml);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
    assertEquals("FATALERROR:\n" + "Fehler: leere XML\n", out.get(0).toString());
  }
  
}
