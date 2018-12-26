package model.dataExchange.json

import com.eclipsesource.schema.drafts.Version7._
import com.eclipsesource.schema.{SchemaArray, SchemaBoolean, SchemaInteger, SchemaNull, SchemaNumber, SchemaObject, SchemaString, SchemaTuple, SchemaType}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}

import scala.util.Try

object JsonSchemaComparator {

  def test(userSchemaString: String, sampleSchemaString: String): JsonSchemaComparisonResult = {
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
            jsonSchemaComparisonResult
        }
    }
  }

  def compare(userSchemaType: SchemaType, sampleSchemaType: SchemaType): JsonSchemaComparisonResult = sampleSchemaType match {
    case sampleObj: SchemaObject      => compareObject(userSchemaType, sampleObj)
    case sampleString: SchemaString   => compareString(userSchemaType, sampleString)
    case sampleNum: SchemaNumber      => compareNumber(userSchemaType, sampleNum)
    case sampleInt: SchemaInteger     => compareInt(userSchemaType, sampleInt)
    case sampleNull: SchemaNull       => compareNull(userSchemaType, sampleNull)
    case sampleArray: SchemaArray     => compareArray(userSchemaType, sampleArray)
    case sampleTuple: SchemaTuple     => compareTuple(userSchemaType, sampleTuple)
    case sampleBoolean: SchemaBoolean => compareBoolean(userSchemaType, sampleBoolean)
  }

  private def compareObject(userSchemaType: SchemaType, sampleObject: SchemaObject): JsonSchemaComparisonResult = userSchemaType match {
    case userObject: SchemaObject =>
      val propertiesMatchingResult = SchemaPropMatcher.doMatch(userObject.properties, sampleObject.properties)

      /*
       * TODO: compare constraints!
       *  -> println(userObject.constraints)
       *  -> println(sampleObject.constraints)
       */

      SchemaObjectComparisonResult(userObject, sampleObject, propertiesMatchingResult)
    case _                        => SchemaTypesDifferent(getType(sampleObject), getType(userSchemaType))
  }

  private def compareString(userSchemaType: SchemaType, sampleString: SchemaString): JsonSchemaComparisonResult = userSchemaType match {
    case userString: SchemaString =>
      /*
       * TODO: compare constraints!
       *  -> println(userString.constraints)
       *  -> println(sampleString.constraints)
       */

      SchemaStringComparisonResult(userString, sampleString)
    case _                        => SchemaTypesDifferent(getType(sampleString), getType(userSchemaType))
  }

  private def compareNumber(userSchemaType: SchemaType, sampleNumber: SchemaNumber): JsonSchemaComparisonResult = userSchemaType match {
    case userNumber: SchemaNumber =>
      /*
       * TODO: more comparisons...
       *  userNumber.constraints
       */

      SchemaNumberComparisonResult(userNumber, sampleNumber)
    case _                        => SchemaTypesDifferent(getType(sampleNumber), getType(userSchemaType))
  }

  private def compareInt(userSchemaType: SchemaType, sampleInteger: SchemaInteger): JsonSchemaComparisonResult = userSchemaType match {
    case userInteger: SchemaInteger => SchemaIntegerComparisonResult(userInteger, sampleInteger)
    case _                          => SchemaTypesDifferent(getType(sampleInteger), getType(userSchemaType))
  }

  private def compareNull(userSchemaType: SchemaType, sampleNull: SchemaNull): JsonSchemaComparisonResult = userSchemaType match {
    case userNull: SchemaNull => SchemaNullComparisonResult(userNull, sampleNull)
    case _                    => SchemaTypesDifferent(getType(sampleNull), getType(userSchemaType))
  }

  private def compareArray(userSchemaType: SchemaType, sampleArray: SchemaArray): JsonSchemaComparisonResult = userSchemaType match {
    case userArray: SchemaArray =>
      val itemComparison = JsonSchemaComparator.compare(userArray.item, sampleArray.item)
      SchemaArrayComparisonResult(userArray, sampleArray, itemComparison)
    case _                      => SchemaTypesDifferent(getType(sampleArray), getType(userSchemaType))
  }

  private def compareTuple(userSchemaType: SchemaType, sampleTuple: SchemaTuple): JsonSchemaComparisonResult = userSchemaType match {
    case userTuple: SchemaTuple => SchemaTupleComparisonResult(userTuple, sampleTuple)
    case _                      => SchemaTypesDifferent(getType(sampleTuple), getType(userSchemaType))
  }

  private def compareBoolean(userSchemaType: SchemaType, sampleBoolean: SchemaBoolean): JsonSchemaComparisonResult = userSchemaType match {
    case userBoolean: SchemaBoolean => SchemaBooleanComparisonResult(userBoolean, sampleBoolean)
    case _                          => SchemaTypesDifferent(getType(sampleBoolean), getType(userSchemaType))
  }

  private def getType(schemaType: SchemaType): String = schemaType match {
    case _: SchemaObject                 => "object"
    case _: SchemaNull                   => "null"
    case _: SchemaNumber                 => "number"
    case _: SchemaInteger                => "integer"
    case _: SchemaArray | _: SchemaTuple => "array"
    case _: SchemaBoolean                => "boolean"
    case _: SchemaString                 => "string"
  }

}
