package model.xml

import java.nio.file.Paths

import model.core.FileUtils
import model.xml.XmlEnums.XmlErrorType
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.scalatest.Matchers._

class XmlCorrectorTest extends FileUtils {

  private val basePath = Paths.get("test", "resources", "xml")

  private def testXmlError(error: XmlError, expectedLine: Int, expectedErrorType: XmlErrorType, expectedMessage: String): Unit = {
    error.line shouldBe expectedLine

    error.errorType shouldBe expectedErrorType

    error.errorMessage shouldBe expectedMessage
  }

  private def assertErrorNum(errorNum: Int, expectedErrors: Int): Unit = errorNum shouldBe expectedErrors

  @Test
  def testClosingTagMissingXmlDtd() {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "partyNoClosingTag.xml")

    assertNotNull(out)
    assertErrorNum(out.size, 1)
    testXmlError(out.head, 8, XmlErrorType.FATAL, """The element type "getraenk" must be terminated by the matching end-tag "</getraenk>".""")
  }

  @Test
  def testCorrectDTDAgainstXML() {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "party.xml")

    assertNotNull(out)
    out.isEmpty shouldBe true
  }

  @Test
  def testCorrectXMLAgainstDTD() {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "party.xml")

    assertNotNull(out)
    out.isEmpty shouldBe true
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
  def testNoRootXmlDtd() {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "partyNoRoot.xml")

    assertErrorNum(out.size, 2)
    testXmlError(out.head, 3, XmlErrorType.ERROR, """Document root element "gast", must match DOCTYPE root "party".""")
    testXmlError(out(1), 8, XmlErrorType.FATAL, "The markup in the document following the root element must be well-formed.")
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
  def testXmlNoElement() {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "xmlNoElement.xml")

    assertErrorNum(out.size, 1)
    testXmlError(out.head, -1, XmlErrorType.FATAL, "Premature end of file.")
  }

}
