package model.xml

import java.nio.file.{Files, Paths}

import model.xml.XmlExType._
import org.junit.Assert.{assertNotNull, assertTrue}
import org.junit.Test
import org.scalatest.Matchers._

import scala.collection.JavaConverters._
import scala.util.Try

class XmlCorrectorTest {

  private val basePath = Paths.get("test", "resources").toString

  private def assertError(error: XmlError, expectedLine: Int, expectedMessage: String): Unit = {
    error.line shouldBe expectedLine

    error.errorMessage shouldBe expectedMessage
  }

  private def assertErrorNum(errorNum: Int, expectedErrors: Int): Unit = errorNum shouldBe expectedErrors


  private def readFile(file: java.nio.file.Path): String = Try(Files.readAllLines(file).asScala.mkString("\n")).getOrElse("")

  @Test
  def emptyXmlAgainstXSD() {
    val xsd = Paths.get(basePath, "xml/note.xsd")

    val out: Seq[XmlError] = XmlCorrector.correct("", readFile(xsd), XML_XSD)

    assertErrorNum(out.size, 1)
    assertError(out.head, -1, "Premature end of file.")
  }

  @Test
  def testClosingTagMissingXmlDtd() {
    val xml = Paths.get(basePath, "xml/partyNoClosingTag.xml")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(xml), "", XML_DTD)

    assertNotNull(out)
    assertErrorNum(out.size, 1)
    assertError(out.head, 17,
      "The element type \"getraenk\" must be terminated by the matching end-tag \"</getraenk>\".")
  }

  @Test
  def testClosingTagMissingXmlXsd() {
    val xml = Paths.get(basePath, "xml/noteNoClosingTag.xml")
    val xsd = Paths.get(basePath, "xml/note.xsd")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(xml), readFile(xsd), XML_XSD)

    assertNotNull(out)
    assertErrorNum(out.size, 1)
    assertError(out.head, 7, "The element type \"to\" must be terminated by the matching end-tag \"</to>\".")
  }

  @Test
  def testCorrectDTDAgainstXML() {
    val referenceFile = Paths.get(basePath, "xml/party.xml")
    val grammar = Paths.get(basePath, "xml/party.dtd")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(referenceFile), readFile(grammar), XML_DTD)

    assertNotNull(out)
    assertTrue(out.isEmpty)
  }

  @Test
  def testCorrectXMLAgainstDTD() {
    val file = Paths.get(basePath, "xml/party.xml")
    val grammar = Paths.get(basePath, "xml/party.dtd")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(file), readFile(grammar), XML_DTD)

    assertNotNull(out)
    assertTrue(out.isEmpty)
  }

  @Test
  def testCorrectXMLAgainstXSD() {
    val xml = Paths.get(basePath, "xml/note.xml")
    val xsd = Paths.get(basePath, "xml/note.xsd")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(xml), readFile(xsd), XML_XSD)

    assertNotNull(out)
    assertTrue(out.isEmpty)
  }

  @Test
  def testMissingAttributeXmlDtd() {
    // val file = Paths.get(basePath, "partyMissingAttribute.xml")
    //
    // Seq<XmlError> out = XmlCorrector.correct(readFile(file), "",
    // XmlExType.XML_DTD)
    //
    // assertErrorNum(out.size, 1)
    // assertError(out.head, 7, XmlErrorType.ERROR,
    // "Attribute \"name\" is required and must be specified for element type
    // \"gast\".")
  }

  @Test
  def testMissingTagXmlXsd() {
    val xml = Paths.get(basePath, "xml/noteMissingTag.xml")
    val xsd = Paths.get(basePath, "xml/note.xsd")

    println(xml.toFile.exists + " :: " + xsd.toFile.exists)

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(xml), readFile(xsd), XML_XSD)

    assertErrorNum(out.size, 1)
    assertError(out.head, 5,
      "cvc-complex-type.2.4.a: Invalid content was found starting with element 'body'. One of '{heading}' is expected.")
  }

  @Test
  def testNoRootXmlDtd() {
    val file = Paths.get(basePath, "xml/partyNoRoot.xml")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(file), "", XML_DTD)

    assertErrorNum(out.size, 1)
    assertError(out.head, 17, "The markup in the document following the root element must be well-formed.")
  }

  @Test
  def testNoRootXmlXsd() {
    val xml = Paths.get(basePath, "xml/noteNoRoot.xml")
    val xsd = Paths.get(basePath, "xml/note.xsd")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(xml), readFile(xsd), XML_XSD)

    assertErrorNum(out.size, 2)
    assertError(out.head, 2, "cvc-elt.1.a: Cannot find the declaration of element 'to'.")
    assertError(out(1), 3, "The markup in the document following the root element must be well-formed.")
  }

  @Test
  def testWrongAttributeDtdXml() {
    // val referenceFile = Paths.get(basePath, "partyNoDate.xml")
    //
    // Seq<XmlError> out = XmlCorrector.correct(readFile(referenceFile),
    // "",
    // XmlExType.DTD_XML)
    //
    // assertErrorNum(out.size, 1)
    // assertError(out.head, 10, XmlErrorType.ERROR, "Attribute \"datum\" must
    // be declared for element type \"party\".")
  }

  @Test
  def testWrongTagXmlDtd() {
    // val file = Paths.get(basePath, "partyWrongTag.xml")
    //
    // Seq<XmlError> out = XmlCorrector.correct(readFile(file), "",
    // XmlExType.XML_DTD)
    //
    // assertErrorNum(out.size, 2)
    // assertError(out.head, 2, XmlErrorType.ERROR, "Element type \"guest\"
    // must be declared.")
    // assertError(out(1), 13, XmlErrorType.ERROR, "The content of element
    // type \"party\" must match \"(gast)*\".")
  }

  @Test
  def testWrongTagXmlXsd() {
    val xml = Paths.get(basePath, "xml/noteWrongTag.xml")
    val xsd = Paths.get(basePath, "xml/note.xsd")

    val out: Seq[XmlError] = XmlCorrector.correct(readFile(xml), readFile(xsd), XML_XSD)

    assertErrorNum(out.size, 1)
    assertError(out.head, 11,
      "cvc-complex-type.2.4.a: Invalid content was found starting with element 'sender'. One of '{from}' is expected.")
  }

  @Test
  def testXmlNoElement() {
    val xml = readFile(Paths.get(basePath, "xml/xmlNoElement.xml"))

    val out: Seq[XmlError] = XmlCorrector.correct(xml, "", XML_DTD)

    assertErrorNum(out.size, 1)
    assertError(out.head, -1, "Premature end of file.")
  }
}
