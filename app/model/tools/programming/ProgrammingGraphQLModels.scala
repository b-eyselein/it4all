package model.tools.programming

import model.graphql.{FilesSolutionToolGraphQLModelBasics, GraphQLArguments}
import model.result.SuccessType
import model.{ExerciseFile, FilesSolution}
import play.api.libs.json.Json
import sangria.macros.derive._
import sangria.schema._

object ProgrammingGraphQLModels
    extends FilesSolutionToolGraphQLModelBasics[ProgrammingExerciseContent, ProgExPart, ProgrammingAbstractResult]
    with GraphQLArguments {

  override val partEnumType: EnumType[ProgExPart] = EnumType(
    "ProgExPart",
    values = ProgExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val unitTestTestConfigType: ObjectType[Unit, UnitTestTestConfig] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val simplifiedUnitTestPartType: ObjectType[Unit, SimplifiedUnitTestPart] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType(ExcludeFields("sampleTestData"))
  }

  private val normalUnitTestPartType: ObjectType[Unit, NormalUnitTestPart] = {
    implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType
    implicit val exFileType: ObjectType[Unit, ExerciseFile]  = exerciseFileType

    deriveObjectType()
  }

  private val implementationPartType: ObjectType[Unit, ImplementationPart] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val unitTestPartType: UnionType[Unit] = UnionType(
    "UnitTestPart",
    types = List(simplifiedUnitTestPartType, normalUnitTestPartType)
  )

  override val exerciseContentType: ObjectType[Unit, ProgrammingExerciseContent] = {
    implicit val ipt: ObjectType[Unit, ImplementationPart] = implementationPartType
    implicit val sst: ObjectType[Unit, FilesSolution]      = solutionType

    deriveObjectType(
      ReplaceField("unitTestPart", Field("unitTestPart", unitTestPartType, resolve = _.value.unitTestPart)),
      AddFields(
        Field(
          "part",
          OptionType(partEnumType),
          arguments = partIdArgument :: Nil,
          resolve = context => ProgExPart.values.find(_.id == context.arg(partIdArgument))
        )
      )
    )
  }

  // Solution types

  private val simplifiedExecutionResultType: ObjectType[Unit, SimplifiedExecutionResult] = {
    implicit val stt: EnumType[SuccessType] = successTypeType

    deriveObjectType(
      ReplaceField(
        "testInput",
        Field("testInput", StringType, resolve = context => Json.stringify(context.value.testInput))
      ),
      ReplaceField("awaited", Field("awaited", StringType, resolve = context => Json.stringify(context.value.awaited))),
      ReplaceField("gotten", Field("gotten", StringType, resolve = context => Json.stringify(context.value.gotten)))
    )
  }

  // Abstract result

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, ProgrammingAbstractResult] =
    InterfaceType(
      "ProgrammingAbstractResult",
      fields[Unit, ProgrammingAbstractResult](
        Field("points", FloatType, resolve = _.value.points.asDouble),
        Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
      ),
      interfaces[Unit, ProgrammingAbstractResult](abstractResultInterfaceType)
    ).withPossibleTypes(() => List(programmingInternalErrorResultType, programmingResultType))

  private val programmingResultType: ObjectType[Unit, ProgrammingResult] = {
    implicit val nert: ObjectType[Unit, NormalExecutionResult]     = deriveObjectType()
    implicit val sert: ObjectType[Unit, SimplifiedExecutionResult] = simplifiedExecutionResultType
    implicit val utcrt: ObjectType[Unit, UnitTestCorrectionResult] = deriveObjectType()

    deriveObjectType[Unit, ProgrammingResult](
      Interfaces(toolAbstractResultTypeInterfaceType),
      ExcludeFields("points", "maxPoints")
    )
  }

  private val programmingInternalErrorResultType: ObjectType[Unit, ProgrammingInternalErrorResult] = deriveObjectType(
    Interfaces(toolAbstractResultTypeInterfaceType),
    ExcludeFields("maxPoints")
  )

}
