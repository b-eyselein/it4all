package model.tools.web

import model.graphql.{FilesSolutionToolGraphQLModelBasics, ToolWithPartsGraphQLModel}
import model.tools.web.sitespec.{HtmlTask, JsAction, JsActionType, SiteSpec}
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import sangria.macros.derive._
import sangria.schema._

import scala.annotation.unused

object WebGraphQLModels
    extends ToolWithPartsGraphQLModel[FilesSolutionInput, WebExerciseContent, WebResult, WebExPart]
    with FilesSolutionToolGraphQLModelBasics[WebExerciseContent, WebResult] {

  override val partEnumType: EnumType[WebExPart] = EnumType(
    "WebExPart",
    values = WebExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  private val HtmlTaskType: ObjectType[Unit, HtmlTask] = ObjectType(
    "HtmlTask",
    fields[Unit, HtmlTask](
      Field("text", StringType, resolve = _.value.text)
    )
  )

  private val jsActionTypeType: EnumType[JsActionType] = deriveEnumType()

  private val jsActionType: ObjectType[Unit, JsAction] = {
    @unused implicit val jatt: EnumType[JsActionType] = jsActionTypeType

    deriveObjectType()
  }

  private val siteSpecType: ObjectType[Unit, SiteSpec] = {
    @unused implicit val htt: ObjectType[Unit, HtmlTask] = HtmlTaskType

    deriveObjectType(
      // TODO: include fields!?!
      ExcludeFields("jsTasks"),
      AddFields(
        Field("htmlTaskCount", IntType, resolve = _.value.htmlTasks.size),
        Field("jsTaskCount", IntType, resolve = _.value.jsTasks.size)
      )
    )
  }

  override val exerciseContentType: ObjectType[Unit, WebExerciseContent] = {
    @unused implicit val siteSpecT: ObjectType[Unit, SiteSpec] = siteSpecType
    @unused implicit val eft: ObjectType[Unit, ExerciseFile]   = exerciseFileType
    @unused implicit val sst: ObjectType[Unit, FilesSolution]  = solutionOutputType

    deriveObjectType()
  }

  // Result types

  private val gradedTextResultType: ObjectType[Unit, GradedTextResult] = deriveObjectType(
    ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
    ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
  )

  implicit val gradedJsActionResultType: ObjectType[Unit, GradedJsActionResult] = {
    @unused implicit val jat: ObjectType[Unit, JsAction] = jsActionType

    deriveObjectType(
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

  private val gradedElementSpecResultType: ObjectType[Unit, GradedElementSpecResult] = {
    @unused implicit val gtrt: ObjectType[Unit, GradedTextResult] = gradedTextResultType

    deriveObjectType(
      AddFields(
        Field("isCorrect", BooleanType, resolve = _.value.isCorrect)
      ),
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

  private val gradedHtmlTaskResultType: ObjectType[Unit, GradedHtmlTaskResult] = {
    @unused implicit val gesrt: ObjectType[Unit, GradedElementSpecResult] = gradedElementSpecResultType

    deriveObjectType()
  }

  private val gradedJsTaskResultType: ObjectType[Unit, GradedJsTaskResult] = {
    @unused implicit val gesrt: ObjectType[Unit, GradedElementSpecResult] = gradedElementSpecResultType

    deriveObjectType(
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)),
      // TODO: seems to be a bug...
      ReplaceField(
        "gradedJsActionResult",
        Field("gradedJsActionResult", gradedJsActionResultType, resolve = _.value.gradedJsActionResult)
      )
    )
  }

  override val resultType: OutputType[WebResult] = {
    @unused implicit val ghtrt: ObjectType[Unit, GradedHtmlTaskResult] = gradedHtmlTaskResultType
    @unused implicit val gjtrt: ObjectType[Unit, GradedJsTaskResult]   = gradedJsTaskResultType

    deriveObjectType[Unit, WebResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
