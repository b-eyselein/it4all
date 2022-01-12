package de.uniwue.dtd.model

sealed trait AttributeType

case object IDAttributeType extends AttributeType {
  override def toString: String = "ID"
}

case object IDRefAttributeType extends AttributeType {
  override def toString: String = "IDREF"
}

case object CDataAttributeType extends AttributeType {
  override def toString: String = "CDATA"
}

final case class EnumAttributeType(values: Seq[String]) extends AttributeType {
  override def toString: String = "(" + values.mkString(" | ") + ")"
}