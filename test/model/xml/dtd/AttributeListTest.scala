package model.xml.dtd

import model.xml.dtd.TestHelperValues._
import org.scalatest.FlatSpec

class AttributeListTest extends FlatSpec {
  "An attribute list" should "contain several values" in {

    // AttributeList with single attribute definition
    for (attrType <- attrTypes; attrSpec <- attrSpecs) {
      val attrDef = AttributeDefinition(attrName, attrType, attrSpec)
      val attlist = AttributeList(elementName, Seq(attrDef))

      assert(attlist.elementName == elementName)
      assert(attlist.attributeDefinitions.lengthCompare(1) == 0)
      assert(attlist.attributeDefinitions.head == attrDef)

      assert(attlist.asString == s"<!ATTLIST $elementName ${attrDef.asString}>")

      assert(AttributeList.unapply(attlist).contains((elementName, Seq(attrDef))))
    }

    val (attrName1, attrName2) = ("attribute1", "attribute2")

    for (attrType1 <- attrTypes; attrSpec1 <- attrSpecs; attrType2 <- attrTypes; attrSpec2 <- attrSpecs) {
      val attrDef1 = AttributeDefinition(attrName1, attrType1, attrSpec1)
      val attrDef2 = AttributeDefinition(attrName2, attrType2, attrSpec2)

      val attrDefs = Seq(attrDef1, attrDef2)

      val attList = AttributeList(elementName, attrDefs)

      assert(attList.elementName == elementName)

      assert(attList.attributeDefinitions.lengthCompare(2) == 0)
      assert(attList.attributeDefinitions == attrDefs)

      assert(attList.asString ==
        s"""<!ATTLIST $elementName
           |    ${attrDef1.asString}
           |    ${attrDef2.asString}
           |>""".stripMargin)

      assert(AttributeList.unapply(attList).contains((elementName, attrDefs)))
    }
  }
}


class AttributeDefinitionTest extends FlatSpec {
  "An attribute definition" should "contain several values" in {

    for (attrType <- attrTypes; attrSpec <- attrSpecs) {
      val definition = AttributeDefinition(attrName, attrType, attrSpec)

      assert(definition.attributeName == attrName)
      assert(definition.attributeType == attrType)
      assert(definition.attributeSpecification == attrSpec)

      assert(definition.asString == attrName + " " + attrType.toString + " " + attrSpec.toString)

      assert(AttributeDefinition.unapply(definition).contains((attrName, attrType, attrSpec)))
    }
  }
}
