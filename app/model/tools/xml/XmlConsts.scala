package model.tools.xml

import model.Consts

object XmlConsts extends Consts {

  val documentName: String = "document"

  val grammarName           : String = "grammar"
  val grammarDescriptionName: String = "grammarDescription"

  val lineName: String = "line"

  val parsedName     : String = "parsed"
  val parseErrorsName: String = "parseErrors"

  val rootNodeName: String = "rootNode"

  val sampleGrammarsName: String = "sampleGrammars"

  val StandardXmlPlayground: String =
    """<?xml version="1.0" encoding="utf-8"?>
      |<!DOCTYPE root [
      |
      |]>""".stripMargin

}
