package model.xml

import model.Consts

object XmlConsts extends Consts {

  val documentName: String = "document"

  val grammarName           : String = "grammar"
  val grammarDescriptionName: String = "grammarDescription"

  val parsedName     : String = "parsed"
  val parseErrorsName: String = "parseErrors"

  val rootNodeName: String = "rootNode"

  val sampleGrammarsName: String = "sampleGrammars"

  val StandardXmlPlayground: String =
    """<?xml version="1.0" encoding="utf-8"?>
      |<!DOCTYPE root [
      |
      |]>""".stripMargin

  val xmlFileEnding: String = "xml"

}
