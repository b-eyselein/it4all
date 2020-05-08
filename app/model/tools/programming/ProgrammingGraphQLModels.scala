package model.tools.programming

import model.{ExerciseFile, SampleSolution}
import model.core.result.SuccessType
import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import play.api.libs.json.Json
import sangria.macros.derive._
import sangria.schema._

object ProgrammingGraphQLModels
    extends ToolGraphQLModelBasics[ProgSolution, ProgrammingExerciseContent, ProgExPart, ProgrammingAbstractResult]
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
      ExcludeFields("inputTypes", "outputType", "sampleTestData")
    )
  }

  // Solution types

  override val SolTypeInputType: InputObjectType[ProgSolution] = {
    implicit val efit: InputObjectType[ExerciseFile] = exerciseFileInputType

    deriveInputObjectType(
      InputObjectTypeName("ProgSolutionInput")
    )
  }

  private val NormalExecutionResultType: ObjectType[Unit, NormalExecutionResult] = {
    implicit val stt: EnumType[SuccessType] = successTypeType

    deriveObjectType()
  }

  private val simplifiedExecutionResultType: ObjectType[Unit, SimplifiedExecutionResult] = {
    implicit val stt: EnumType[SuccessType] = successTypeType

    deriveObjectType(
      ReplaceField("input", Field("input", StringType, resolve = context => Json.stringify(context.value.input))),
      ReplaceField("awaited", Field("awaited", StringType, resolve = context => Json.stringify(context.value.awaited))),
      ReplaceField("gotten", Field("gotten", StringType, resolve = context => Json.stringify(context.value.gotten)))
    )
  }

  private val unitTestCorrectionResultType: ObjectType[Unit, UnitTestCorrectionResult] = {
    implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType

    deriveObjectType()
  }

  // Abstract result

  private val programmingAbstractResultType: InterfaceType[Unit, ProgrammingAbstractResult] = InterfaceType(
    "ProgrammingAbstractResult",
    fields[Unit, ProgrammingAbstractResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    ),
    interfaces[Unit, ProgrammingAbstractResult](abstractResultInterfaceType)
  ).withPossibleTypes(() => List(programmingInternalErrorResultType, programmingResultType))

  private val programmingResultType: ObjectType[Unit, ProgrammingResult] = {
    implicit val nert: ObjectType[Unit, NormalExecutionResult]     = NormalExecutionResultType
    implicit val sert: ObjectType[Unit, SimplifiedExecutionResult] = simplifiedExecutionResultType
    implicit val utcrt: ObjectType[Unit, UnitTestCorrectionResult] = unitTestCorrectionResultType

    deriveObjectType[Unit, ProgrammingResult](
      Interfaces(programmingAbstractResultType)
    )
  }

  private val programmingInternalErrorResultType: ObjectType[Unit, ProgrammingInternalErrorResult] = deriveObjectType(
    Interfaces(programmingAbstractResultType),
    ExcludeFields("maxPoints")
  )

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, ProgrammingAbstractResult] =
    programmingAbstractResultType
}
