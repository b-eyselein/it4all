package model.xml

import model.Consts

object XmlConsts extends Consts {

  val GrammarDescriptionName = "grammarDescription"

  val RefFileContentName = "refFileContent"
  val RootNodeName       = "rootNode"

  val STANDARD_XML_PLAYGROUND: String =
    """<?xml version="1.0" encoding="utf-8"?>
      |<!DOCTYPE root [
      |
      |]>""".stripMargin

  val XML_FILE_ENDING = "xml"

}
