package de.uniwue.dtd.model

sealed trait AttributeSpecification

case object RequiredSpecification extends AttributeSpecification {
  override def toString: String = "#REQUIRED"
}

case object ImpliedSpecification extends AttributeSpecification {
  override def toString: String = "#IMPLIED"
}

final case class DefaultValueSpecification(default: String) extends AttributeSpecification {
  override def toString: String = "\"" + default + "\""
}

final case class FixedValueSpecification(value: String) extends AttributeSpecification {
  override def toString: String = "#FIXED \"" + value + "\""
}