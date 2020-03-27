package model.tools.collectionTools.sql

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{AddFields, ExcludeFields, deriveEnumType, deriveObjectType}
import sangria.schema.{EnumType, Field, InputType, IntType, ObjectType, OptionType, StringType}

object SqlGraphQLModels extends ToolGraphQLModelBasics[SqlExerciseContent, String, SqlResult] {

  override val ExContentTypeType: ObjectType[Unit, SqlExerciseContent] = {
    implicit val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

    implicit val sqlSampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType()
  }

  // Solution types

  override val SolTypeInputType: InputType[String] = StringType

  // Result types

  private val sqlQueriesStaticComparisonType: ObjectType[Unit, SqlQueriesStaticComparison] = {
    deriveObjectType(
      // TODO: do not exclude fields...
      AddFields(Field("X", OptionType(IntType), resolve = _ => None)),
      ExcludeFields(
        "columnComparison",
        "tableComparison",
        "joinExpressionComparison",
        "whereComparison",
        "additionalComparisons"
      )
    )
  }

  private val sqlExecutionResultType: ObjectType[Unit, SqlExecutionResult] = {

    deriveObjectType()
  }

  override val CompResultTypeType: ObjectType[Unit, SqlResult] = {
    implicit val sqsct: ObjectType[Unit, SqlQueriesStaticComparison] = sqlQueriesStaticComparisonType
    implicit val sert: ObjectType[Unit, SqlExecutionResult]          = sqlExecutionResultType

    deriveObjectType()
  }

}
