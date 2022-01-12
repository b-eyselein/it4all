package de.uniwue.dtd.model

import de.uniwue.dtd.model.TestHelperValues._
import org.scalatest.FlatSpec

class AttributeTypeTest extends FlatSpec {

  "An ID attribute type" should "have a toString representation" in {
    assert(IDAttributeType.toString == "ID")
  }

  "An IDREF attribute type" should "have a toString representation" in {
    assert(IDRefAttributeType.toString == "IDREF")
  }

  "A CDATA attribute type" should "have a toString representation" in {
    assert(CDataAttributeType.toString == "CDATA")
  }

  "An enum attribute type" should "have certain values and a certain toString representation" in {
    val attrType = EnumAttributeType(enumValues)

    assert(attrType.values == enumValues)
    assert(attrType.toString == "(" + enumValues.mkString(" | ") + ")")

    assert(EnumAttributeType.unapply(attrType).contains(enumValues))
  }

}
