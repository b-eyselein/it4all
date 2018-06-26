package model.xml

import model.Consts

object XmlConsts extends Consts {

  val grammarName = "grammar"

  val grammarDescriptionName = "grammarDescription"

  val RootNodeName = "rootNode"

  val sampleGrammarsName = "sampleGrammars"

  val STANDARD_XML_PLAYGROUND: String =
    """<?xml version="1.0" encoding="utf-8"?>
      |<!DOCTYPE root [
      |
      |]>""".stripMargin

  val XML_FILE_ENDING = "xml"

}
