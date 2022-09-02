package model.tools.flask

import model.graphql.{FilesSolutionToolGraphQLModelBasics, ToolGraphQLModel}
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import sangria.macros.derive._
import sangria.schema._

object FlaskToolGraphQLModels
    extends ToolGraphQLModel[FilesSolutionInput, FlaskExerciseContent, FlaskResult]
    with FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskResult] {

  private val flaskTestsConfigType: ObjectType[Unit, FlaskTestsConfig] = {
    implicit val flaskSingleTestConfigType: ObjectType[Unit, FlaskSingleTestConfig] = deriveObjectType()

    deriveObjectType()
  }

  override val exerciseContentType: ObjectType[Unit, FlaskExerciseContent] = {
    implicit val eft: ObjectType[Unit, ExerciseFile]      = exerciseFileType
    implicit val ftct: ObjectType[Unit, FlaskTestsConfig] = flaskTestsConfigType
    implicit val sst: ObjectType[Unit, FilesSolution]     = solutionOutputType

    deriveObjectType()
  }

  override val resultType: OutputType[FlaskResult] = {
    implicit val flaskTestResultType: ObjectType[Unit, FlaskTestResult] = deriveObjectType()

    deriveObjectType[Unit, FlaskResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
