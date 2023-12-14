package model.tools.flask

import model.graphql.{FilesSolutionToolGraphQLModelBasics, GraphQLContext, ToolGraphQLModel}
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import sangria.macros.derive._
import sangria.schema._

import scala.annotation.unused

object FlaskToolGraphQLModels
    extends ToolGraphQLModel[FilesSolutionInput, FlaskExerciseContent, FlaskResult]
    with FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskResult] {

  private val flaskTestsConfigType: ObjectType[Unit, FlaskTestsConfig] = {
    @unused implicit val flaskSingleTestConfigType: ObjectType[Unit, FlaskSingleTestConfig] = deriveObjectType()

    deriveObjectType()
  }

  override val exerciseContentType: ObjectType[Unit, FlaskExerciseContent] = {
    @unused implicit val eft: ObjectType[GraphQLContext, ExerciseFile]  = ExerciseFile.queryType
    @unused implicit val ftct: ObjectType[Unit, FlaskTestsConfig]       = flaskTestsConfigType
    @unused implicit val sst: ObjectType[GraphQLContext, FilesSolution] = FilesSolution.queryType

    deriveObjectType()
  }

  override val resultType: OutputType[FlaskResult] = {
    @unused implicit val flaskTestResultType: ObjectType[Unit, FlaskTestResult] = deriveObjectType()

    deriveObjectType[Unit, FlaskResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
