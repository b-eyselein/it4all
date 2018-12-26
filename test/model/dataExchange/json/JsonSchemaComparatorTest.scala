package model.dataExchange.json

import play.api.libs.json.Json
import org.scalatest.{FlatSpec, Matchers}
import better.files._

class JsonSchemaComparatorTest extends FlatSpec with Matchers {

  private val basePath: File = "test" / "resources" / "dataExchange" / "json"

  private val userSchemaString: String =
    """{
      |  "type": "object",
      |  "properties": {
      |    "family_name": {"type": "string"}
      |  }
      |}""".stripMargin

  private val sampleSchemaString: String =
    """{
      |  "type": "object",
      |  "properties": {
      |    "first_name": {"type": "string"},
      |    "family_name": {"type": "string"}
      |  },
      |  "required": ["first_name", "family_name"]
      |}""".stripMargin

  "The JsonSchemaTester" should "do something" in {
    val comparison = JsonSchemaComparator.test(userSchemaString, sampleSchemaString)
    println(Json.prettyPrint(comparison.toJson))
    fail("TODO!")
  }

  it should "parse bools" in {
    val fileContent = (basePath / "bool.schema.json").contentAsString

    assert(JsonSchemaComparator.test(fileContent, fileContent).isSuccessfull)
  }

  it should "parse nulls" in {
    val fileContent = (basePath / "null.schema.json").contentAsString

    assert(JsonSchemaComparator.test(fileContent, fileContent).isSuccessfull)
  }

  it should "parse strings" in {
    val fileContent = (basePath / "string.schema.json").contentAsString

    assert(JsonSchemaComparator.test(fileContent, fileContent).isSuccessfull)
  }

  it should "parse numbers" in {
    val fileContent = (basePath / "number.schema.json").contentAsString

    assert(JsonSchemaComparator.test(fileContent, fileContent).isSuccessfull)
  }

  it should "parse integers" in {
    val fileContent = (basePath / "integer.schema.json").contentAsString

    assert(JsonSchemaComparator.test(fileContent, fileContent).isSuccessfull)
  }

  it should "parse empty objects" in {
    val fileContent = (basePath / "empty_object.schema.json").contentAsString

    assert(JsonSchemaComparator.test(fileContent, fileContent).isSuccessfull)
  }

  it should "parse non-empty objects" in {
    val fileContent = (basePath / "object.schema.json").contentAsString

    assert(JsonSchemaComparator.test(fileContent, fileContent).isSuccessfull)
  }

}
