package model.xml.dtd

object TestHelperValues {

  val enumValues   = Seq("test1", "test2", "test3")
  val DefaultValue = "My Default Value"
  val FixedValue   = "My Fixed Value"

  val elementName = "root"

  val attrName  = "myAttrName"
  val attrTypes = Seq(IDAttributeType, IDRefAttributeType, CDataAttributeType, EnumAttributeType(enumValues))
  val attrSpecs = Seq(RequiredSpecification, ImpliedSpecification, DefaultValueSpecification(DefaultValue), FixedValueSpecification(FixedValue))

}
