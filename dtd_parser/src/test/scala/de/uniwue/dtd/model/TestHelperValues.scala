package de.uniwue.dtd.model

object TestHelperValues {

  val enumValues  : Seq[String] = Seq("test1", "test2", "test3")
  val DefaultValue: String      = "My Default Value"
  val FixedValue  : String      = "My Fixed Value"

  val elementName: String = "root"

  val attrName : String                      = "myAttrName"
  val attrTypes: Seq[AttributeType]          = Seq(IDAttributeType, IDRefAttributeType, CDataAttributeType, EnumAttributeType(enumValues))
  val attrSpecs: Seq[AttributeSpecification] = Seq(RequiredSpecification, ImpliedSpecification, DefaultValueSpecification(DefaultValue), FixedValueSpecification(FixedValue))

}
