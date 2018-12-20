package model.dataExchange.json

import com.eclipsesource.schema.SchemaType
import com.eclipsesource.schema.drafts.Version7._
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

import scala.util.Try

object JsonSchema {

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

  def test(): Unit = {
    val parsedUserSchema: JsValue = Try(Json.parse(userSchemaString)).getOrElse(???)
    val parsedSampleSchema: JsValue = Try(Json.parse(sampleSchemaString)).getOrElse(???)

    Json.fromJson[SchemaType](parsedSampleSchema) match {
      case JsError(sampleErrors)      =>
        sampleErrors.foreach(println)
        ???
      case JsSuccess(sampleSchema, _) =>
        Json.fromJson[SchemaType](parsedUserSchema) match {
          case JsError(userErrors)      =>
            userErrors.foreach(println)
            ???
          case JsSuccess(userSchema, _) =>
            val jsonSchemaComparisonResult: JsonSchemaComparisonResult = JsonSchemaComparator.compare(userSchema, sampleSchema)
            println(Json.prettyPrint(jsonSchemaComparisonResult.toJson))
        }
    }
  }


}
