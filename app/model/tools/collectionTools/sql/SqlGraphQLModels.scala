package model.tools.collectionTools.sql

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{deriveEnumType, deriveObjectType}
import sangria.schema.{EnumType, InputType, ObjectType, StringType}

object SqlGraphQLModels extends ToolGraphQLModelBasics[SqlExerciseContent, String] {

  override val ExContentTypeType: ObjectType[Unit, SqlExerciseContent] = {
    implicit val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

    implicit val sqlSampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType()
  }

  override val SolTypeInputType: InputType[String] = StringType

}
