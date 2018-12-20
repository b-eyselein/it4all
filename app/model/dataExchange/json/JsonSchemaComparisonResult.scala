package model.dataExchange.json

import com.eclipsesource.schema._
import model.core.matching.MatchingResult
import play.api.libs.json.{JsValue, Json}

sealed trait JsonSchemaComparisonResult {

  def toJson: JsValue = ???

}

case object SchemaTypesDifferent extends JsonSchemaComparisonResult

sealed trait JsonSchemaTypeComparisonResult[T <: SchemaType] extends JsonSchemaComparisonResult {
  val userArg  : T
  val sampleArg: T

  override def toJson: JsValue = Json.obj(
    "userArg" -> userArg.toString(),
    "sampleArg" -> sampleArg.toString()
  )

}

final case class SchemaObjectComparisonResult(userArg: SchemaObject, sampleArg: SchemaObject, propertyMatchingResult: MatchingResult[SchemaPropMatch])
  extends JsonSchemaTypeComparisonResult[SchemaObject] {

  override def toJson: JsValue = Json.obj(
    "userArg" -> userArg.toString(),
    "sampleArg" -> sampleArg.toString(),
    "propertyMatches" -> propertyMatchingResult.toJson,
  )

}

final case class SchemaStringComparisonResult(userArg: SchemaString, sampleArg: SchemaString)
  extends JsonSchemaTypeComparisonResult[SchemaString]

final case class SchemaNumberComparisonResult(userArg: SchemaNumber, sampleArg: SchemaNumber)
  extends JsonSchemaTypeComparisonResult[SchemaNumber]

final case class SchemaIntegerComparisonResult(userArg: SchemaInteger, sampleArg: SchemaInteger)
  extends JsonSchemaTypeComparisonResult[SchemaInteger]

final case class SchemaNullComparisonResult(userArg: SchemaNull, sampleArg: SchemaNull)
  extends JsonSchemaTypeComparisonResult[SchemaNull]

final case class SchemaArrayComparisonResult(userArg: SchemaArray, sampleArg: SchemaArray, itemComparison: JsonSchemaComparisonResult)
  extends JsonSchemaTypeComparisonResult[SchemaArray]

final case class SchemaBooleanComparisonResult(userArg: SchemaBoolean, sampleArg: SchemaBoolean)
  extends JsonSchemaTypeComparisonResult[SchemaBoolean]

final case class SchemaTupleComparisonResult(userArg: SchemaTuple, sampleArg: SchemaTuple)
  extends JsonSchemaTypeComparisonResult[SchemaTuple]