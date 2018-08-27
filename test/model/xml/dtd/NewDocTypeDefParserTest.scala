package model.xml.dtd

import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit.Test

import scala.util.{Failure, Success, Try}

class NewDocTypeDefParserTest {

  @Test
  def testWrongElements(): Unit = {
    val strsToParse: Seq[String] = Seq(
      "<!Element name (#PCDATA)>", "<!ELEM name (#PCDATA)>", "<ELEMENT name (#PCDATA)>", "<!ELEMENT name <"
    )

    strsToParse.foreach(str => DocTypeDefParser.parseDtdLine(str) match {
      case Failure(error: DTDParseException) => assertThat(error.getMessage, equalTo("Line can not be identified as element or attlist!"))
      case Failure(error)                    => fail(error.getMessage)
      case _                                 => fail("TODO!")
    })
  }

  @Test
  def testWrongAttLists(): Unit = {
    val strsToParse: Seq[String] = Seq(
      "<!Attlist name ...>", "<!ATT name ...>", "<ATTLIST name ...>", "<!ATTLIST name <"
    )

    strsToParse.foreach(str => DocTypeDefParser.parseDtdLine(str) match {
      case Failure(error: DTDParseException) => assertThat(error.getMessage, equalTo("Line can not be identified as element or attlist!"))
      case Failure(error)                    => fail(error.getMessage)
      case _                                 => fail("TODO!")
    })
  }


  @Test
  def newTest(): Unit = {
    val parsed: Try[DocTypeDef] = DocTypeDefParser.tryParseDTD(
      """<!ATTLIST gast
        |  ledig (true | false) #REQUIRED
        |  nuechtern (true | false) #REQUIRED
        |>""".stripMargin)

    parsed match {
      case Success(doctypeDef) => Unit
      case Failure(error)      => fail(error.getMessage)
    }
  }

}
