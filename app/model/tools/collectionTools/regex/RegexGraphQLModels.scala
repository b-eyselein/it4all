package model.tools.collectionTools.regex

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{deriveEnumType, deriveObjectType}
import sangria.schema.{EnumType, InputType, ObjectType, StringType}

object RegexGraphQLModels extends ToolGraphQLModelBasics[RegexExerciseContent, String, RegexCompleteResult] {

  override val ExContentTypeType: ObjectType[Unit, RegexExerciseContent] = {
    implicit val regeexCorrectionTypeType: EnumType[RegexCorrectionType] = deriveEnumType()

    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    implicit val rmtdt: ObjectType[Unit, RegexMatchTestData] = deriveObjectType()

    implicit val retdt: ObjectType[Unit, RegexExtractionTestData] = deriveObjectType()

    deriveObjectType()
  }

  override val SolTypeInputType : InputType[String] = StringType

  override val CompResultTypeType: ObjectType[Unit, RegexCompleteResult] = deriveObjectType()

}
