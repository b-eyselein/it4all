package model;

import java.io.File;
import java.util.List;

public class MainTester {
  
  public static void main(String[] args) {
    
    File xml = new File("test/resources/empty.xml");
    List<XMLError> out = XmlCorrector.correctXMLAgainstDTD(xml);
    
    System.out.println(out.size());
    
    for(XMLError xmlError: out) {
      System.out.println(xmlError);
    }
    
//    try {
//      out = XmlCorrector.correctXMLAgainstXSD(xsd, file);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//    Assert.assertTrue(out.size() == 1);
//    Assert.assertEquals(
//        "ERROR:" + "\n" + "Zeile: 5" + "\n" + "Fehler: "
//            + "cvc-complex-type.2.4.a: Invalid content was found starting with element 'body'. One of '{heading}' is expected.\n",
//        out.get(0).getErrorMessage());
//    System.out.println(out.size());
  }
}
