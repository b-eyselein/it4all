package model.dataExchange.json

import com.eclipsesource.schema._
import model.core.matching.MatchingResult
import play.api.libs.json.{JsValue, Json}

sealed trait JsonSchemaComparisonResult {

  def toJson: JsValue = ???

  def isSuccessfull: Boolean

}

final case class SchemaTypesDifferent(typeAwaited: String, typeGotten: String) extends JsonSchemaComparisonResult {
  override def isSuccessfull: Boolean = false
}

sealed trait JsonSchemaTypeComparisonResult[T <: SchemaType] extends JsonSchemaComparisonResult {
  val userArg  : T
  val sampleArg: T

  override def toJson: JsValue = Json.obj(
    "userArg" -> userArg.toString(),
    "sampleArg" -> sampleArg.toString()
  )

}

// Objects

final case class SchemaObjectComparisonResult(userArg: SchemaObject, sampleArg: SchemaObject, propertyMatchingResult: MatchingResult[SchemaPropMatch])
  extends JsonSchemaTypeComparisonResult[SchemaObject] {

  override def toJson: JsValue = Json.obj(
    "userArg" -> userArg.toString(),
    "sampleArg" -> sampleArg.toString(),
    "propertyMatches" -> ??? //propertyMatchingResult.toJson,
  )

  override def isSuccessfull: Boolean = propertyMatchingResult.allMatches.forall(_.subComparison.exists(_.isSuccessfull))

}

// ArrayLike Types

final case class SchemaArrayComparisonResult(userArg: SchemaArray, sampleArg: SchemaArray, itemComparison: JsonSchemaComparisonResult)
  extends JsonSchemaTypeComparisonResult[SchemaArray] {

  override def isSuccessfull: Boolean = itemComparison.isSuccessfull

}

final case class SchemaTupleComparisonResult(userArg: SchemaTuple, sampleArg: SchemaTuple)
  extends JsonSchemaTypeComparisonResult[SchemaTuple] {

  override def isSuccessfull: Boolean = false

}

// Results for 'scalar' comparisons

final case class SchemaStringComparisonResult(userArg: SchemaString, sampleArg: SchemaString)
  extends JsonSchemaTypeComparisonResult[SchemaString] {

  override def isSuccessfull: Boolean = true

}

final case class SchemaNumberComparisonResult(userArg: SchemaNumber, sampleArg: SchemaNumber)
  extends JsonSchemaTypeComparisonResult[SchemaNumber] {

  override def isSuccessfull: Boolean = true

}

final case class SchemaIntegerComparisonResult(userArg: SchemaInteger, sampleArg: SchemaInteger)
  extends JsonSchemaTypeComparisonResult[SchemaInteger] {

  override def isSuccessfull: Boolean = true

}

final case class SchemaNullComparisonResult(userArg: SchemaNull, sampleArg: SchemaNull)
  extends JsonSchemaTypeComparisonResult[SchemaNull] {

  override def isSuccessfull: Boolean = true

}

final case class SchemaBooleanComparisonResult(userArg: SchemaBoolean, sampleArg: SchemaBoolean)
  extends JsonSchemaTypeComparisonResult[SchemaBoolean] {

  override def isSuccessfull: Boolean = true

}
