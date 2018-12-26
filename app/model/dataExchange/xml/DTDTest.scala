package model.dataExchange.xml

import scala.xml.parsing.ConstructingParser

object DTDTest {


  def main(args: Array[String]): Unit = {
    val src = scala.io.Source.fromFile("test/resources/dataExchange/xml/test.dtd")

    println(src)

    val parser: ConstructingParser = new scala.xml.parsing.ConstructingParser(src, false)

    val doc = parser.parseDTD

    println(doc)
  }

}