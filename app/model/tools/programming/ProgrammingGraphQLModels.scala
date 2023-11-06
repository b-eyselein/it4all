package model.tools.programming

import model.graphql.{FilesSolutionToolGraphQLModelBasics, ToolWithPartsGraphQLModel}
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import sangria.macros.derive._
import sangria.schema._

import scala.annotation.unused

object ProgrammingGraphQLModels
    extends ToolWithPartsGraphQLModel[FilesSolutionInput, ProgrammingExerciseContent, ProgrammingResult, ProgExPart]
    with FilesSolutionToolGraphQLModelBasics[ProgrammingExerciseContent, ProgrammingResult] {

  override val partEnumType: EnumType[ProgExPart] = EnumType(
    "ProgExPart",
    values = ProgExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val unitTestTestConfigType: ObjectType[Unit, UnitTestTestConfig] = {
    @unused implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val unitTestPartType: ObjectType[Unit, UnitTestPart] = {
    @unused implicit val uttct: ObjectType[Unit, UnitTestTestConfig] = unitTestTestConfigType
    @unused implicit val exFileType: ObjectType[Unit, ExerciseFile]  = exerciseFileType

    deriveObjectType()
  }

  private val implementationPartType: ObjectType[Unit, ImplementationPart] = {
    @unused implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  override val exerciseContentType: ObjectType[Unit, ProgrammingExerciseContent] = {
    @unused implicit val upt: ObjectType[Unit, UnitTestPart]       = unitTestPartType
    @unused implicit val ipt: ObjectType[Unit, ImplementationPart] = implementationPartType
    @unused implicit val sst: ObjectType[Unit, FilesSolution]      = solutionOutputType

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
    @unused implicit val nert: ObjectType[Unit, ImplementationCorrectionResult] = deriveObjectType(
      Interfaces(programmingTestCorrectionResultInterfaceType)
    )

    @unused implicit val utcrt: ObjectType[Unit, UnitTestCorrectionResult] = deriveObjectType(
      Interfaces(programmingTestCorrectionResultInterfaceType)
    )

    deriveObjectType[Unit, ProgrammingResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
