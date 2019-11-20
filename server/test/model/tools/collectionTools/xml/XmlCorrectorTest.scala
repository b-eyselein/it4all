package model.tools.collectionTools.xml

import java.nio.file.Paths

import better.files.File._
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.scalatest.Matchers._

class XmlCorrectorTest { //extends FileUtils {

  private val basePath = Paths.get("test", "resources", "xml")

  private def testXmlError(error: XmlError, expectedLine: Int, expectedErrorType: XmlErrorType, expectedMessage: String): Unit = {
    error.line shouldBe expectedLine

    error.errorType shouldBe expectedErrorType

    error.errorMessage shouldBe expectedMessage
  }

  private def assertErrorNum(errorNum: Int, expectedErrors: Int): Unit = errorNum shouldBe expectedErrors

  @Test
  def testClosingTagMissingXmlDtd(): Unit = {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "partyNoClosingTag.xml")

    assertNotNull(out)
    assertErrorNum(out.size, 1)
    testXmlError(out.head, 8, XmlErrorType.FATAL, """The element type "getraenk" must be terminated by the matching end-tag "</getraenk>".""")
  }

  @Test
  def testCorrectDTDAgainstXML(): Unit = {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "party.xml")

    assertNotNull(out)
    out.isEmpty shouldBe true
  }

  @Test
  def testCorrectXMLAgainstDTD(): Unit = {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "party.xml")

    assertNotNull(out)
    out.isEmpty shouldBe true
  }

  @Test
  def testNoRootXmlDtd(): Unit = {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "partyNoRoot.xml")

    assertErrorNum(out.size, 2)
    testXmlError(out.head, 3, XmlErrorType.ERROR, """Document root element "gast", must match DOCTYPE root "party".""")
    testXmlError(out(1), 8, XmlErrorType.FATAL, "The markup in the document following the root element must be well-formed.")
  }

  @Test
  def testXmlNoElement(): Unit = {
    val out = XmlCorrector.correctAgainstMentionedDTD(basePath / "xmlNoElement.xml")

    assertErrorNum(out.size, 1)
    testXmlError(out.head, -1, XmlErrorType.FATAL, "Premature end of file.")
  }

}
