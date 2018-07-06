package model.xml.dtd

import java.nio.file.Paths

import model.core.FileUtils
import org.scalatest.FlatSpec

import scala.util.{Failure, Success}

class DocTypeDefParserTest extends FlatSpec with FileUtils {

  private def testParseError[T](parseSpec: DocTypeDefParser.Parser[T], toParse: String): Unit = {
    assert(DocTypeDefParser.parse(parseSpec, toParse).isInstanceOf[DocTypeDefParser.NoSuccess])
  }

  private def testParse[T](parseSpec: DocTypeDefParser.Parser[T], toParse: String, awaited: T): Unit = {
    val parsed: DocTypeDefParser.ParseResult[T] = DocTypeDefParser.parse(parseSpec, toParse)
    parsed match {
      case DocTypeDefParser.Success(res, _)   => assert(res == awaited)
      case DocTypeDefParser.NoSuccess(msg, _) => fail("Parsing failed with msg: " + msg)
    }
  }

  val testData = Seq("MyDef", "My Default TestData", "This is my def testdata")

  // Attribute specifications

  "The DTDParser" should " parse a default value specification for an attribute " in {
    for (td <- testData) {
      testParse(DocTypeDefParser.defaultValueSpec, "\"" + td + "\"", DefaultValueSpecification(td))
    }
  }

  it should "parse a fixed value spec for an attribute" in {
    for (td <- testData) {
      testParse(DocTypeDefParser.fixedValueSpec, "#FIXED \"" + td + "\"", FixedValueSpecification(td))
    }
  }

  it should "parse a implied spec for an attribute" in {
    testParse(DocTypeDefParser.impliedSpec, "#IMPLIED", ImpliedSpecification)
    testParseError(DocTypeDefParser.impliedSpec, "#REQUIRED")
  }


  it should "parse a required spec for an attribute" in {
    testParse(DocTypeDefParser.requiredSpec, "#REQUIRED", RequiredSpecification)
    testParseError(DocTypeDefParser.requiredSpec, "#IMPLIED")
  }

  it should "parse an attribute spec" in {
    for (td <- testData) {
      testParse(DocTypeDefParser.attrSpec, "\"" + td + "\"", DefaultValueSpecification(td))
      testParse(DocTypeDefParser.attrSpec, "#FIXED \"" + td + "\"", FixedValueSpecification(td))
    }
    testParse(DocTypeDefParser.attrSpec, "#IMPLIED", ImpliedSpecification)
    testParse(DocTypeDefParser.attrSpec, "#REQUIRED", RequiredSpecification)
  }

  // Attribute types

  it should "parse an ID type for an attribute" in {
    testParse(DocTypeDefParser.idAttrType, "ID", IDAttributeType)
    testParseError(DocTypeDefParser.idAttrType, "IDREF")
  }

  it should "parse an IDREF type for an attribute" in {
    testParse(DocTypeDefParser.idRefAttrType, "IDREF", IDRefAttributeType)
    testParseError(DocTypeDefParser.idRefAttrType, "ID")
  }

  it should "parse an CDATA type for an attribute" in {
    testParse(DocTypeDefParser.cDataAttrType, "CDATA", CDataAttributeType)
    testParseError(DocTypeDefParser.cDataAttrType, "ID")
  }
  it should "parse an enum type for an attribute" in {
    testParse(DocTypeDefParser.enumAttrType, "(test1 | test2 | test3)", EnumAttributeType(Seq("test1", "test2", "test3")))
    testParseError(DocTypeDefParser.enumAttrType, "ID")
  }

  it should "parse the type of an attribute" in {
    testParse(DocTypeDefParser.attributeType, "ID", IDAttributeType)
    testParse(DocTypeDefParser.attributeType, "IDREF", IDRefAttributeType)
    testParse(DocTypeDefParser.attributeType, "CDATA", CDataAttributeType)
    testParse(DocTypeDefParser.attributeType, "(test1 | test2 | test3)", EnumAttributeType(Seq("test1", "test2", "test3")))
  }

  it should "parse a dtd" in {
    val file = Paths.get("test", "resources", "xml", "note.dtd")
    if (file.toFile.exists()) {
      readAll(file) match {
        case Failure(error)   => fail(error.toString)
        case Success(content) =>
          DocTypeDefParser.tryParseDTD(content) match {
            case Failure(error)   => fail(error.toString)
            case Success(grammar) => assert(grammar.asString == content)
          }
      }
    } else {
      fail("A file does not exist!")
    }
  }

}
