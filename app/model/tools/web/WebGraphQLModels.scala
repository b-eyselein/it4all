package model.tools.web

import de.uniwue.webtester.sitespec.{HtmlTask, JsAction, JsActionType, SiteSpec}
import model.core.result.SuccessType
import model.tools.{ExerciseFile, SampleSolution, SemanticVersion, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object WebGraphQLModels extends ToolGraphQLModelBasics[WebExercise, WebSolution, WebExPart] {

  private val HtmlTaskType: ObjectType[Unit, HtmlTask] = ObjectType(
    "HtmlTask",
    fields[Unit, HtmlTask](
      Field("text", StringType, resolve = _.value.text)
    )
  )

  private val jsActionTypeType: EnumType[JsActionType] = deriveEnumType()

  private val jsActionType: ObjectType[Unit, JsAction] = {
    implicit val jatt: EnumType[JsActionType] = jsActionTypeType

    deriveObjectType()
  }

  private val siteSpecType: ObjectType[Unit, SiteSpec] = {
    implicit val htt: ObjectType[Unit, HtmlTask] = HtmlTaskType

    deriveObjectType(
      // TODO: include fields!?!
      ExcludeFields("jsTasks"),
      AddFields(
        Field("htmlTaskCount", IntType, resolve = _.value.htmlTasks.size),
        Field("jsTaskCount", IntType, resolve = _.value.jsTasks.size)
      )
    )
  }

  private val webSolutionType: ObjectType[Unit, WebSolution] = {
    implicit val eft: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  private val webExTagType: EnumType[WebExTag] = deriveEnumType()

  override val ExerciseType: ObjectType[Unit, WebExercise] = {
    implicit val svt: ObjectType[Unit, SemanticVersion] = semanticVersionType
    implicit val wett: EnumType[WebExTag]               = webExTagType
    implicit val siteSpecT: ObjectType[Unit, SiteSpec]  = siteSpecType
    implicit val eft: ObjectType[Unit, ExerciseFile]    = exerciseFileType
    implicit val sampleSolType: ObjectType[Unit, SampleSolution[WebSolution]] =
      sampleSolutionType("Web", webSolutionType)

    deriveObjectType(
      Interfaces(exerciseInterfaceType)
    )
  }

  // Solution types

  override val SolTypeInputType: InputType[WebSolution] = {
    implicit val eft: InputObjectType[ExerciseFile] = exerciseFileInputType

    deriveInputObjectType[WebSolution](
      InputObjectTypeName("WebSolutionInput")
    )
  }

  // Result types

  private val gradedTextResultType: ObjectType[Unit, GradedTextResult] = deriveObjectType(
    ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
    ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
  )

  private val gradedHtmlTaskResultType: ObjectType[Unit, GradedHtmlTaskResult] = {
    implicit val stt: EnumType[SuccessType]               = successTypeType
    implicit val gtrt: ObjectType[Unit, GradedTextResult] = gradedTextResultType

    deriveObjectType(
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

  implicit val gradedJsActionResultType: ObjectType[Unit, GradedJsActionResult] = {
    implicit val jat: ObjectType[Unit, JsAction] = jsActionType

    deriveObjectType(
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

  private val gradedJsHtmlElementSpecResultType: ObjectType[Unit, GradedJsHtmlElementSpecResult] = {
    implicit val stt: EnumType[SuccessType]               = successTypeType
    implicit val gtrt: ObjectType[Unit, GradedTextResult] = gradedTextResultType

    deriveObjectType(
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

  private val gradedJsTaskResultType: ObjectType[Unit, GradedJsTaskResult] = {
    implicit val stt: EnumType[SuccessType]                               = successTypeType
    implicit val gjtesrt: ObjectType[Unit, GradedJsHtmlElementSpecResult] = gradedJsHtmlElementSpecResultType

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

  private val webCompleteResultType: ObjectType[Unit, WebCompleteResult] = {
    implicit val ghtrt: ObjectType[Unit, GradedHtmlTaskResult] = gradedHtmlTaskResultType
    implicit val gjtrt: ObjectType[Unit, GradedJsTaskResult]   = gradedJsTaskResultType

    deriveObjectType(
      Interfaces(abstractResultTypeType),
      ExcludeFields("solutionSaved", "points", "maxPoints")
    )
  }

  override val AbstractResultTypeType: OutputType[Any] = webCompleteResultType

  // Part type

  override val PartTypeInputType: EnumType[WebExPart] = EnumType(
    "WebExPart",
    values = WebExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
