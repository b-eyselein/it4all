package model

import model.XmlExType._

import scala.collection.mutable.ListBuffer

object XmlExType {

  val values: ListBuffer[XmlExType] = ListBuffer.empty

  def byName(n: String): Option[XmlExType] = values.find(_.name == n)

}

abstract sealed class XmlExType(val name: String, val studFileEnding: String, val refFileEnding: String, val gramFileEnding: String)
  extends model.exercise.Tag {

  values += this

  override val getButtonContent: String = name

  override def getTitle: String = {
    val forTitle = name.split("_")
    s"${forTitle(0)} gegen ${forTitle(1)}"
  }

}

case object XML_XSD extends XmlExType("XML_XSD", "xml", "xsd", "xsd")

case object XML_DTD extends XmlExType("XML_DTD", "xml", "dtd", "dtd")

case object XSD_XML extends XmlExType("XSD_XML", "xsd", "xml", "xsd")

case object DTD_XML extends XmlExType("DTD_XML", "dtd", "xml", "dtd")

