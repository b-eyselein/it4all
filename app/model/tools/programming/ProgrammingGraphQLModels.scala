package model.tools.programming

import model.GraphQLArguments
import model.core.result.SuccessType
import model.tools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object ProgrammingGraphQLModels
    extends ToolGraphQLModelBasics[ProgSolution, ProgrammingExerciseContent, ProgExPart]
    with GraphQLArguments {

  override val partEnumType: EnumType[ProgExPart] = EnumType(
    "ProgExPart",
    values = ProgExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

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

    deriveObjectType()
  }

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[ProgSolution]] =
    buildSampleSolutionType("Programming", progSolutionType)

  override val exerciseContentType: ObjectType[Unit, ProgrammingExerciseContent] = {
    //    implicit val progInputType: ObjectType[Unit, ProgInput] = deriveObjectType()
    //    implicit val progDataType: ObjectType[Unit, ProgDataType] = deriveObjectType()

    implicit val utpt: ObjectType[Unit, UnitTestPart]                = unitTestPartType
    implicit val ipt: ObjectType[Unit, ImplementationPart]           = implementationPartType
    implicit val sst: ObjectType[Unit, SampleSolution[ProgSolution]] = sampleSolutionType

    deriveObjectType(
      AddFields(
        Field(
          "part",
          OptionType(partEnumType),
          arguments = partIdArgument :: Nil,
          resolve = context => ProgExPart.values.find(_.id == context.arg(partIdArgument))
        )
      ),
      // TODO: include fields !?!
      ExcludeFields("inputTypes", "outputType", "baseData", "sampleTestData", "maybeClassDiagramPart")
    )
  }

  // Solution types

  override val SolTypeInputType: InputObjectType[ProgSolution] = {
    implicit val efit: InputObjectType[ExerciseFile] = exerciseFileInputType

    deriveInputObjectType(
      InputObjectTypeName("ProgSolutionInput")
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

}
