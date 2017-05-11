package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

public class XmlCorrectorTest {

  private static final Path BASE = Paths.get("test", "resources");

  private static void assertError(XmlError error, int expectedLine, XmlErrorType expectedType, String expectedMessage) {
    assertThat("Fehler sollte in Zeile " + expectedLine + " sein, war aber in Zeile " + error.getLine(),
        error.getLine(), equalTo(expectedLine));

    assertThat("Typ des Fehlers sollte " + expectedType + " sein, war aber " + error.getErrorType(),
        error.getErrorType(), equalTo(expectedType));

    assertThat(error.getErrorMessage(), equalTo(expectedMessage));
  }

  private static void assertErrorNum(int errorNum, int expectedErrors) {
    assertThat("Es sollte(n) " + expectedErrors + " Fehler sein, es sind aber " + errorNum + "!", errorNum,
        equalTo(expectedErrors));
  }

  private static String readFile(Path file) {
    try {
      return String.join("\n", Files.readAllLines(file));
    } catch (IOException e) {
      return "";
    }
  }

  @Test
  public void emptyXmlAgainstXSD() {
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct("", readFile(xsd), XmlExType.XML_XSD);

    assertErrorNum(out.size(), 1);
    assertError(out.get(0), -1, XmlErrorType.FATALERROR, "Premature end of file.");
  }

  @Test
  public void testClosingTagMissingXmlDtd() {
    Path xml = Paths.get(BASE.toString(), "partyNoClosingTag.xml");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), "", XmlExType.XML_DTD);

    assertNotNull(out);
    assertErrorNum(out.size(), 1);
    assertError(out.get(0), 17, XmlErrorType.FATALERROR,
        "The element type \"getraenk\" must be terminated by the matching end-tag \"</getraenk>\".");
  }

  @Test
  public void testClosingTagMissingXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteNoClosingTag.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);

    assertNotNull(out);
    assertErrorNum(out.size(), 1);
    assertError(out.get(0), 7, XmlErrorType.FATALERROR,
        "The element type \"to\" must be terminated by the matching end-tag \"</to>\".");
  }

  @Test
  public void testCorrectDTDAgainstXML() {
    Path referenceFile = Paths.get(BASE.toString(), "party.xml");
    Path grammar = Paths.get(BASE.toString(), "party.dtd");

    List<XmlError> out = XmlCorrector.correct(readFile(referenceFile), readFile(grammar), XmlExType.XML_DTD);

    assertNotNull(out);
    assertTrue(out.isEmpty());
  }

  @Test
  public void testCorrectXMLAgainstDTD() {
    Path file = Paths.get(BASE.toString(), "party.xml");
    Path grammar = Paths.get(BASE.toString(), "party.dtd");

    List<XmlError> out = XmlCorrector.correct(readFile(file), readFile(grammar), XmlExType.XML_DTD);

    assertNotNull(out);
    assertTrue(out.isEmpty());
  }

  @Test
  public void testCorrectXMLAgainstXSD() {
    Path xml = Paths.get(BASE.toString(), "note.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);

    assertNotNull(out);
    assertTrue(out.isEmpty());
  }

  @Test
  public void testMissingAttributeXmlDtd() {
    // Path file = Paths.get(BASE.toString(), "partyMissingAttribute.xml");
    //
    // List<XmlError> out = XmlCorrector.correct(readFile(file), "",
    // XmlExType.XML_DTD);
    //
    // assertErrorNum(out.size(), 1);
    // assertError(out.get(0), 7, XmlErrorType.ERROR,
    // "Attribute \"name\" is required and must be specified for element type
    // \"gast\".");
  }

  @Test
  public void testMissingTagXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteMissingTag.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);

    assertErrorNum(out.size(), 1);
    assertError(out.get(0), 5, XmlErrorType.ERROR,
        "cvc-complex-type.2.4.a: Invalid content was found starting with element 'body'. One of '{heading}' is expected.");
  }

  @Test
  public void testNoRootXmlDtd() {
    Path file = Paths.get(BASE.toString(), "partyNoRoot.xml");

    List<XmlError> out = XmlCorrector.correct(readFile(file), "", XmlExType.XML_DTD);

    assertErrorNum(out.size(), 1);
    assertError(out.get(0), 17, XmlErrorType.FATALERROR,
        "The markup in the document following the root element must be well-formed.");
  }

  @Test
  public void testNoRootXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteNoRoot.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);

    assertErrorNum(out.size(), 2);
    assertError(out.get(0), 2, XmlErrorType.ERROR, "cvc-elt.1.a: Cannot find the declaration of element 'to'.");
    assertError(out.get(1), 3, XmlErrorType.FATALERROR,
        "The markup in the document following the root element must be well-formed.");
  }

  @Test
  public void testWrongAttributeDtdXml() {
    // Path referenceFile = Paths.get(BASE.toString(), "partyNoDate.xml");
    //
    // List<XmlError> out = XmlCorrector.correct(readFile(referenceFile), "",
    // XmlExType.DTD_XML);
    //
    // assertErrorNum(out.size(), 1);
    // assertError(out.get(0), 10, XmlErrorType.ERROR, "Attribute \"datum\" must
    // be declared for element type \"party\".");
  }

  @Test
  public void testWrongTagXmlDtd() {
    // Path file = Paths.get(BASE.toString(), "partyWrongTag.xml");
    //
    // List<XmlError> out = XmlCorrector.correct(readFile(file), "",
    // XmlExType.XML_DTD);
    //
    // assertErrorNum(out.size(), 2);
    // assertError(out.get(0), 2, XmlErrorType.ERROR, "Element type \"guest\"
    // must be declared.");
    // assertError(out.get(1), 13, XmlErrorType.ERROR, "The content of element
    // type \"party\" must match \"(gast)*\".");
  }

  @Test
  public void testWrongTagXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteWrongTag.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);

    assertErrorNum(out.size(), 1);
    assertError(out.get(0), 11, XmlErrorType.ERROR,
        "cvc-complex-type.2.4.a: Invalid content was found starting with element 'sender'. One of '{from}' is expected.");
  }

  @Test
  public void xmlNoElement() {
    String xml = readFile(Paths.get(BASE.toString(), "xmlNoElement.xml"));

    List<XmlError> out = XmlCorrector.correct(xml, "", XmlExType.XML_DTD);

    assertErrorNum(out.size(), 1);
    assertError(out.get(0), -1, XmlErrorType.FATALERROR, "Premature end of file.");
  }
}
