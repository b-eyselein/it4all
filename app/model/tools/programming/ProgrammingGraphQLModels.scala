package model.tools.programming

import model.graphql.{FilesSolutionToolGraphQLModelBasics, GraphQLArguments}
import model.{ExerciseFile, FilesSolution}
import sangria.macros.derive._
import sangria.schema._

object ProgrammingGraphQLModels extends FilesSolutionToolGraphQLModelBasics[ProgrammingExerciseContent, ProgrammingResult, ProgExPart] with GraphQLArguments {

  override val partEnumType: EnumType[ProgExPart] = EnumType(
    "ProgExPart",
    values = ProgExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val unitTestTestConfigType: ObjectType[Unit, UnitTestTestConfig] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val unitTestPartType: ObjectType[Unit, UnitTestPart] = {
    implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType
    implicit val exFileType: ObjectType[Unit, ExerciseFile]  = exerciseFileType

    deriveObjectType()
  }

  private val implementationPartType: ObjectType[Unit, ImplementationPart] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  override val exerciseContentType: ObjectType[Unit, ProgrammingExerciseContent] = {
    implicit val upt: ObjectType[Unit, UnitTestPart]       = unitTestPartType
    implicit val ipt: ObjectType[Unit, ImplementationPart] = implementationPartType
    implicit val sst: ObjectType[Unit, FilesSolution]      = solutionOutputType

    deriveObjectType()
  }

  private val programmingTestCorrectionResultInterfaceType: InterfaceType[Unit, ProgrammingTestCorrectionResult] =
    InterfaceType(
      "ProgrammingTestCorrectionResult",
      fields[Unit, ProgrammingTestCorrectionResult](
        Field("successful", BooleanType, resolve = _.value.successful)
      )
    )

  override val resultType: OutputType[ProgrammingResult] = {
    implicit val nert: ObjectType[Unit, ImplementationCorrectionResult] = deriveObjectType(
      Interfaces(programmingTestCorrectionResultInterfaceType)
    )
    implicit val utcrt: ObjectType[Unit, UnitTestCorrectionResult] = deriveObjectType(
      Interfaces(programmingTestCorrectionResultInterfaceType)
    )

    deriveObjectType[Unit, ProgrammingResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
