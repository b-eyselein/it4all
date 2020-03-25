package model.tools.collectionTools.sql

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{deriveEnumType, deriveObjectType}
import sangria.schema.{EnumType, ObjectType}

object SqlGraphQLModels extends ToolGraphQLModelBasics[SqlExerciseContent] {

  override val ExContentTypeType: ObjectType[Unit, SqlExerciseContent] = {
    implicit val sqlExerciseTypeType: EnumType[SqlExerciseType] = deriveEnumType()

    implicit val sqlSampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType()
  }

}
