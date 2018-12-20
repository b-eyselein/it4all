package model.dataExchange.json

import com.eclipsesource.schema.{SchemaArray, SchemaInteger, SchemaNull, SchemaNumber, SchemaObject, SchemaString, SchemaTuple, SchemaType}

object JsonSchemaComparator {

  def compare(userSchema: SchemaType, sampleSchema: SchemaType): JsonSchemaComparisonResult = (userSchema, sampleSchema) match {
    case (userObj: SchemaObject, sampleObj: SchemaObject) => compareObject(userObj, sampleObj)

    case (userString: SchemaString, sampleString: SchemaString) => compareString(userString, sampleString)

    case (userNum: SchemaNumber, sampleNum: SchemaNumber) => compareNumber(userNum, sampleNum)

    case (userInt: SchemaInteger, sampleInt: SchemaInteger) => compareInt(userInt, sampleInt)

    case (userNull: SchemaNull, sampleNull: SchemaNull) => compareNull(userNull, sampleNull)

    case (userArray: SchemaArray, sampleArray: SchemaArray) => compareArray(userArray, sampleArray)

    case (userTuple: SchemaTuple, sampleTuple: SchemaTuple) => compareTuple(userTuple, sampleTuple)

    case (userOther, sampleOther) =>
      println(userOther.getClass + " :: " + sampleOther.getClass)
      SchemaTypesDifferent
  }

  private def compareObject(userObject: SchemaObject, sampleObject: SchemaObject): JsonSchemaComparisonResult = {
    val propertiesMatchingResult = SchemaPropMatcher.doMatch(userObject.properties, sampleObject.properties)

    /*
     * TODO: compare constraints!
     *  -> println(userObject.constraints)
     *  -> println(sampleObject.constraints)
     */

    SchemaObjectComparisonResult(userObject, sampleObject, propertiesMatchingResult)
  }

  private def compareString(userString: SchemaString, sampleString: SchemaString): JsonSchemaComparisonResult = {
    /*
     * TODO: compare constraints!
     *  -> println(userString.constraints)
     *  -> println(sampleString.constraints)
     */

    SchemaStringComparisonResult(userString, sampleString)
  }

  private def compareNumber(userNumber: SchemaNumber, sampleNumber: SchemaNumber): JsonSchemaComparisonResult = {
    // TODO: more comparisons...
    userNumber.constraints

    SchemaNumberComparisonResult(userNumber, sampleNumber)
  }

  private def compareInt(userInteger: SchemaInteger, sampleInteger: SchemaInteger): JsonSchemaComparisonResult = {
    SchemaIntegerComparisonResult(userInteger, sampleInteger)
  }

  private def compareNull(userNull: SchemaNull, sampleNull: SchemaNull): JsonSchemaComparisonResult = {
    SchemaNullComparisonResult(userNull, sampleNull)
  }

  private def compareArray(userArray: SchemaArray, sampleArray: SchemaArray): JsonSchemaComparisonResult = {
    val itemComparison = JsonSchemaComparator.compare(userArray.item, sampleArray.item)

    SchemaArrayComparisonResult(userArray, sampleArray, itemComparison)
  }

  private def compareTuple(userTuple: SchemaTuple, sampleTuple: SchemaTuple): JsonSchemaComparisonResult = {
    SchemaTupleComparisonResult(userTuple, sampleTuple)
  }

}
