package model.tools.flask

import model.graphql.FilesSolutionToolGraphQLModelBasics
import model.{ExerciseFile, FilesSolution}
import sangria.macros.derive._
import sangria.schema._

object FlaskToolGraphQLModels
    extends FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskExPart, FlaskAbstractResult] {

  private val flaskTestsConfigType: ObjectType[Unit, FlaskTestsConfig] = {
    implicit val flaskSingleTestConfigType: ObjectType[Unit, FlaskSingleTestConfig] = deriveObjectType()

    deriveObjectType()
  }

  override val partEnumType: EnumType[FlaskExPart] = EnumType(
    "FlaskExercisePart",
    values = FlaskExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val exerciseContentType: ObjectType[Unit, FlaskExerciseContent] = {
    implicit val eft: ObjectType[Unit, ExerciseFile]      = exerciseFileType
    implicit val ftct: ObjectType[Unit, FlaskTestsConfig] = flaskTestsConfigType
    implicit val sst: ObjectType[Unit, FilesSolution]     = solutionType

    deriveObjectType()
  }

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, FlaskAbstractResult] = InterfaceType(
    "FlaskAbstractCorrectionResult",
    fields[Unit, FlaskAbstractResult](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    ),
    interfaces[Unit, FlaskAbstractResult](abstractResultInterfaceType)
  ).withPossibleTypes(() => List(flaskCorrectionResultType, flaskInternalErrorResultType))

  private val flaskCorrectionResultType: ObjectType[Unit, FlaskResult] = {
    implicit val flaskTestResultType: ObjectType[Unit, FlaskTestResult] = deriveObjectType()

    deriveObjectType(
      Interfaces(toolAbstractResultTypeInterfaceType),
      ExcludeFields("points", "maxPoints")
    )
  }

  private val flaskInternalErrorResultType: ObjectType[Unit, FlaskInternalErrorResult] = {
    deriveObjectType(
      Interfaces(toolAbstractResultTypeInterfaceType),
      ExcludeFields("maxPoints")
    )
  }

}
