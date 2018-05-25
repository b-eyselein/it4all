package model.xml.dtd

sealed trait AttributeSpecification

case object RequiredSpecification extends AttributeSpecification {
  override def toString: String = "#REQUIRED"
}

case object ImpliedSpecification extends AttributeSpecification {
  override def toString: String = "#IMPLIED"
}

case class DefaultValueSpecification(default: String) extends AttributeSpecification {
  override def toString: String = "\"" + default + "\""
}

case class FixedValueSpecification(value: String) extends AttributeSpecification {
  override def toString: String = "#FIXED \"" + value + "\""
}