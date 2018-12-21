package model.dataExchange.json

import com.eclipsesource.schema.{SchemaArray, SchemaInteger, SchemaNull, SchemaNumber, SchemaObject, SchemaString, SchemaTuple, SchemaType}

object JsonSchemaComparator {

  def compare(userSchema: SchemaType, sampleSchema: SchemaType): JsonSchemaComparisonResult = sampleSchema match {
    case sampleObj: SchemaObject    => compareObject(userSchema, sampleObj)
    case sampleString: SchemaString => compareString(userSchema, sampleString)
    case sampleNum: SchemaNumber    => compareNumber(userSchema, sampleNum)
    case sampleInt: SchemaInteger   => compareInt(userSchema, sampleInt)
    case sampleNull: SchemaNull     => compareNull(userSchema, sampleNull)
    case sampleArray: SchemaArray   => compareArray(userSchema, sampleArray)
    case sampleTuple: SchemaTuple   => compareTuple(userSchema, sampleTuple)
    case sampleOther                =>
      println(userSchema.getClass + " :: " + sampleOther.getClass)
      SchemaTypesDifferent
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
    case _                        => SchemaTypesDifferent
  }

  private def compareString(userSchemaType: SchemaType, sampleString: SchemaString): JsonSchemaComparisonResult = userSchemaType match {
    case userString: SchemaString =>
      /*
       * TODO: compare constraints!
       *  -> println(userString.constraints)
       *  -> println(sampleString.constraints)
       */

      SchemaStringComparisonResult(userString, sampleString)
    case _                        => SchemaTypesDifferent
  }

  private def compareNumber(userSchemaType: SchemaType, sampleNumber: SchemaNumber): JsonSchemaComparisonResult = userSchemaType match {
    case userNumber: SchemaNumber =>
      /*
       * TODO: more comparisons...
       *  userNumber.constraints
       */

      SchemaNumberComparisonResult(userNumber, sampleNumber)
    case _                        => SchemaTypesDifferent
  }

  private def compareInt(userSchemaType: SchemaType, sampleInteger: SchemaInteger): JsonSchemaComparisonResult = userSchemaType match {
    case userInteger: SchemaInteger => SchemaIntegerComparisonResult(userInteger, sampleInteger)
    case _                          => SchemaTypesDifferent
  }

  private def compareNull(userSchemaType: SchemaType, sampleNull: SchemaNull): JsonSchemaComparisonResult = userSchemaType match {
    case userNull: SchemaNull => SchemaNullComparisonResult(userNull, sampleNull)
    case _                    => SchemaTypesDifferent
  }

  private def compareArray(userSchemaType: SchemaType, sampleArray: SchemaArray): JsonSchemaComparisonResult = userSchemaType match {
    case userArray: SchemaArray =>
      val itemComparison = JsonSchemaComparator.compare(userArray.item, sampleArray.item)
      SchemaArrayComparisonResult(userArray, sampleArray, itemComparison)
    case _                      => SchemaTypesDifferent
  }

  private def compareTuple(userSchemaType: SchemaType, sampleTuple: SchemaTuple): JsonSchemaComparisonResult = userSchemaType match {
    case userTuple: SchemaTuple => SchemaTupleComparisonResult(userTuple, sampleTuple)
    case _                      => SchemaTypesDifferent
  }

}
