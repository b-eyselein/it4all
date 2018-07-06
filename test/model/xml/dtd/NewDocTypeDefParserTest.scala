package model.xml.dtd

import org.hamcrest.CoreMatchers._
import org.junit.Assert._
import org.junit.Test

import scala.util.Failure

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


}
