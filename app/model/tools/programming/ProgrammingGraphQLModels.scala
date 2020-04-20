package model.tools.programming

import model.core.result.SuccessType
import model.tools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object ProgrammingGraphQLModels extends ToolGraphQLModelBasics[ProgSolution, ProgrammingExerciseContent, ProgExPart] {

  private val unitTestTestConfigType: ObjectType[Unit, UnitTestTestConfig] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val unitTestPartType: ObjectType[Unit, UnitTestPart] = {
    implicit val unitTestTypeType: EnumType[UnitTestType] = deriveEnumType()

    implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType

    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val implementationPartType: ObjectType[Unit, ImplementationPart] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val progSolutionType: ObjectType[Unit, ProgSolution] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    //  implicit val progTestDataType: ObjectType[Unit, ProgTestData] = deriveObjectType(
    //     ExcludeFields("input", "output")
    //  )

    deriveObjectType(ExcludeFields("testData"))
  }

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[ProgSolution]] =
    buildSampleSolutionType("Programming", progSolutionType)

  private val programmingExerciseTagType: EnumType[ProgrammingExerciseTag] = deriveEnumType()

  override val exerciseContentType: ObjectType[Unit, ProgrammingExerciseContent] = {
    //    implicit val progInputType: ObjectType[Unit, ProgInput] = deriveObjectType()
    //    implicit val progDataType: ObjectType[Unit, ProgDataType] = deriveObjectType()

    implicit val pett: EnumType[ProgrammingExerciseTag]    = programmingExerciseTagType
    implicit val utpt: ObjectType[Unit, UnitTestPart]      = unitTestPartType
    implicit val ipt: ObjectType[Unit, ImplementationPart] = implementationPartType

    deriveObjectType(
      // TODO: include fields !?!
      ExcludeFields("inputTypes", "outputType", "baseData", "sampleTestData", "maybeClassDiagramPart")
    )
  }

  // Solution types

  override val SolTypeInputType: InputObjectType[ProgSolution] = {
    implicit val efit: InputObjectType[ExerciseFile] = exerciseFileInputType

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

  override val AbstractResultTypeType: OutputType[Any] = {
    implicit val nert: ObjectType[Unit, NormalExecutionResult]     = NormalExecutionResultType
    implicit val utcrt: ObjectType[Unit, UnitTestCorrectionResult] = unitTestCorrectionResultType

    deriveObjectType[Unit, ProgCompleteResult](
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
