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
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(error.getErrorMessage(), equalTo("Premature end of file."));
  }

  @Test
  public void testClosingTagMissingXmlDtd() {
    Path xml = Paths.get(BASE.toString(), "partyNoClosingTag.xml");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), "", XmlExType.XML_DTD);

    assertNotNull(out);
    assertThat("Es sollte nur ein Fehler sein, es sind aber " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.errorType, equalTo(XmlErrorType.FATALERROR));
    assertThat(error.line, equalTo(6));
    assertThat(error.errorMessage,
        equalTo("The element type \"getraenk\" must be terminated by the matching end-tag \"</getraenk>\"."));
  }

  @Test
  public void testClosingTagMissingXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteNoClosingTag.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);

    assertNotNull(out);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.errorType, equalTo(XmlErrorType.FATALERROR));
    assertThat(error.line, equalTo(7));
    assertThat(error.errorMessage,
        equalTo("The element type \"to\" must be terminated by the matching end-tag \"</to>\"."));
  }

  @Test
  public void testCorrectDTDAgainstXML() {
    Path referenceFile = Paths.get(BASE.toString(), "party.xml");
    Path grammar = Paths.get(BASE.toString(), "party.dtd");

    List<XmlError> out = XmlCorrector.correct(readFile(referenceFile), readFile(grammar), XmlExType.XML_DTD);
    assertTrue(out.isEmpty());
  }

  @Test
  public void testCorrectXMLAgainstDTD() {
    Path file = Paths.get(BASE.toString(), "party.xml");
    Path grammar = Paths.get(BASE.toString(), "party.dtd");

    List<XmlError> out = XmlCorrector.correct(readFile(file), readFile(grammar), XmlExType.XML_DTD);
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
  public void testMissingTagXmlDtd() {
    Path file = Paths.get(BASE.toString(), "partyMissingAttribute.xml");

    List<XmlError> out = XmlCorrector.correct(readFile(file), "", XmlExType.XML_DTD);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(7));
    assertThat(error.getErrorMessage(),
        equalTo("Attribute \"name\" is required and must be specified for element type \"gast\"."));
  }

  @Test
  public void testMissingTagXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteMissingTag.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(5));
    assertThat(error.getErrorMessage(), equalTo(
        "cvc-complex-type.2.4.a: Invalid content was found starting with element 'body'. One of '{heading}' is expected."));
  }

  @Test
  public void testNoPartyDateDtdXml() {
    Path referenceFile = Paths.get(BASE.toString(), "partyNoDate.xml");
    Path grammar = Paths.get(BASE.toString(), "party.dtd");

    List<XmlError> out = XmlCorrector.correct(readFile(referenceFile), readFile(grammar), XmlExType.DTD_XML);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getErrorMessage(), equalTo("Attribute \"datum\" must be declared for element type \"party\"."));
  }

  @Test
  public void testNoRootXmlDtd() {
    Path file = Paths.get(BASE.toString(), "partyNoRoot.xml");

    List<XmlError> out = XmlCorrector.correct(readFile(file), "", XmlExType.XML_DTD);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(error.getLine(), equalTo(1));
    assertThat(error.getErrorMessage(), equalTo("Document root element \"gast\", must match DOCTYPE root \"party\"."));
  }

  @Test
  public void testNoRootXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteNoRoot.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(2));

    XmlError error = out.get(0);
    XmlError fatalError = out.get(1);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(2));
    assertThat(error.getErrorMessage(), equalTo("cvc-elt.1.a: Cannot find the declaration of element 'to'."));

    assertThat(fatalError.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(fatalError.getLine(), equalTo(3));
    assertThat(fatalError.getErrorMessage(),
        equalTo("The markup in the document following the root element must be well-formed."));
  }

  @Test
  public void testWrongTagXmlDtd() {
    Path file = Paths.get(BASE.toString(), "partyWrongTag.xml");

    List<XmlError> out = XmlCorrector.correct(readFile(file), "", XmlExType.XML_DTD);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(2));

    XmlError firstError = out.get(0);
    XmlError secondError = out.get(1);

    assertThat(firstError.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(firstError.getLine(), equalTo(2));
    assertThat(firstError.getErrorMessage(), equalTo("Element type \"guest\" must be declared."));

    assertThat(secondError.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(secondError.getLine(), equalTo(13));
    assertThat(secondError.getErrorMessage(), equalTo("The content of element type \"party\" must match \"(gast)*\"."));
  }

  @Test
  public void testWrongTagXmlXsd() {
    Path xml = Paths.get(BASE.toString(), "noteWrongTag.xml");
    Path xsd = Paths.get(BASE.toString(), "note.xsd");

    List<XmlError> out = XmlCorrector.correct(readFile(xml), readFile(xsd), XmlExType.XML_XSD);
    assertThat("Sollte nur ein Fehler sein, aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.ERROR));
    assertThat(error.getLine(), equalTo(5));
    assertThat(error.getErrorMessage(), equalTo(
        "cvc-complex-type.2.4.a: Invalid content was found starting with element 'sender'. One of '{from}' is expected."));
  }

  @Test
  public void xmlNoElement() {
    String xml = readFile(Paths.get(BASE.toString(), "xmlNoElement.xml"));

    List<XmlError> out = XmlCorrector.correct(xml, "", XmlExType.XML_DTD);
    assertThat("Es sollte genau ein Fehler sein, es aber sind " + out.size() + " Fehler!", out.size(), equalTo(1));

    XmlError error = out.get(0);

    assertThat(error.getErrorType(), equalTo(XmlErrorType.FATALERROR));
    assertThat(error.getErrorMessage(), equalTo("Premature end of file."));
  }
}
