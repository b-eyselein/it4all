package model.tools.collectionTools.programming

import model.core.result.SuccessType
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema.{EnumType, EnumValue, InputObjectType, InputType, ObjectType}

object ProgrammingGraphQLModels
    extends ToolGraphQLModelBasics[ProgExerciseContent, ProgSolution, ProgCompleteResult, ProgExPart] {

  private val unitTestTestConfigType: ObjectType[Unit, UnitTestTestConfig] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    deriveObjectType()
  }

  private val unitTestPartType: ObjectType[Unit, UnitTestPart] = {
    implicit val unitTestTypeType: EnumType[UnitTestType] = deriveEnumType()

    implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType

    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    deriveObjectType()
  }

  private val implementationPartType: ObjectType[Unit, ImplementationPart] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    deriveObjectType()
  }

  private val progSolutionType: ObjectType[Unit, ProgSolution] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    //  implicit val progTestDataType: ObjectType[Unit, ProgTestData] = deriveObjectType(
    //     ExcludeFields("input", "output")
    //  )

    deriveObjectType(ExcludeFields("testData"))
  }

  override val ExContentTypeType: ObjectType[Unit, ProgExerciseContent] = {
    //    implicit val progInputType: ObjectType[Unit, ProgInput] = deriveObjectType()
    //    implicit val progDataType: ObjectType[Unit, ProgDataType] = deriveObjectType()

    implicit val utpt: ObjectType[Unit, UnitTestPart] = unitTestPartType

    implicit val ipt: ObjectType[Unit, ImplementationPart] = implementationPartType

    implicit val sst: ObjectType[Unit, SampleSolution[ProgSolution]] = sampleSolutionType("Prog", progSolutionType)

    deriveObjectType(
      // TODO: include fields !?!
      ExcludeFields("inputTypes", "outputType", "baseData", "sampleTestData", "maybeClassDiagramPart")
    )
  }

  // Solution types

  override val SolTypeInputType: InputObjectType[ProgSolution] = {
    implicit val efit: InputObjectType[ExerciseFile] = ExerciseFileInputType

    deriveInputObjectType(
      InputObjectTypeName("ProgSolutionInput"),
      ExcludeInputFields("testData")
    )
  }

  // Result types

  private val NormalExecutionResultType: ObjectType[Unit, NormalExecutionResult] = {
    implicit val stt: EnumType[SuccessType] = successTypeType

    deriveObjectType()
  }

  private val unitTestCorrectionResultType: ObjectType[Unit, UnitTestCorrectionResult] = {
    implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType

    deriveObjectType()
  }

  override val AbstractResultTypeType: ObjectType[Unit, ProgCompleteResult] = {
    implicit val nert: ObjectType[Unit, NormalExecutionResult]     = NormalExecutionResultType
    implicit val utcrt: ObjectType[Unit, UnitTestCorrectionResult] = unitTestCorrectionResultType

    deriveObjectType(
      // FIXME: do not exclude fields anymore...
      ExcludeFields("simplifiedResults")
    )
  }

  // Parts

  override val PartTypeInputType: EnumType[ProgExPart] = EnumType(
    "ProgExPart",
    values = ProgExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
