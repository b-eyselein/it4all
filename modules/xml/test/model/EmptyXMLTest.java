package model;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

public class EmptyXMLTest {
  
  @Test
  public void emptyXML() {
    // FIXME: behebe NullpointerException!
    File xml = new File("test/resources/empty.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(xml);
    assertEquals("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), 1);
  }

}
