package model.xml

import model.Consts

object XmlConsts extends Consts {

  val REF_FILE_CONTENT_NAME = "refFileContent"
  val ROOT_NODE_NAME        = "rootNode"

  val STANDARD_XML_PLAYGROUND: String =
    """<?xml version="1.0" encoding="utf-8"?>
      |<!DOCTYPE root [
      |
      |]>""".stripMargin

  val XML_FILE_ENDING = "xml"

}