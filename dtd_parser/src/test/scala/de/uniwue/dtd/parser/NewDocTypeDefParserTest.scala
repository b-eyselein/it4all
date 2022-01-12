package de.uniwue.dtd.parser

import de.uniwue.dtd.model.DocTypeDef
import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Success, Try}

class NewDocTypeDefParserTest extends FlatSpec with Matchers {

  "A DocTypeDefParser" should "not parse wrong elements" in {
    val strsToParse: Seq[String] = Seq(
      "<!Element name (#PCDATA)>", "<!ELEM name (#PCDATA)>", "<ELEMENT name (#PCDATA)>", "<!ELEMENT name <"
    )

    strsToParse.foreach(str => DocTypeDefParser.parseDtdLine(str) match {
      case Failure(error: DTDParseException) => assert(error.getMessage == "Line can not be identified as element or attlist!")
      case Failure(error)                    => fail(error.getMessage)
      case _                                 => fail("TODO!")
    })
  }

  it should "not parse wrong attlists" in {
    val strsToParse: Seq[String] = Seq(
      "<!Attlist name ...>", "<!ATT name ...>", "<ATTLIST name ...>", "<!ATTLIST name <"
    )

    strsToParse.foreach(str => DocTypeDefParser.parseDtdLine(str) match {
      case Failure(error: DTDParseException) => assert(error.getMessage == "Line can not be identified as element or attlist!")
      case Failure(error)                    => fail(error.getMessage)
      case _                                 => fail("TODO!")
    })
  }

  it should "parse a correct attlist" in {
    val parsed: Try[DocTypeDef] = DocTypeDefParser.tryParseDTD(
      """<!ATTLIST gast
        |  ledig (true | false) #REQUIRED
        |  nuechtern (true | false) #REQUIRED
        |>""".stripMargin)

    parsed match {
      case Success(doctypeDef) => ()
      case Failure(error)      => fail(error.getMessage)
    }
  }

}
