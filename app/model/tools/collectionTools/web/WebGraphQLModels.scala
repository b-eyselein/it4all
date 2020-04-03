package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.{HtmlTask, JsAction, JsActionType, SiteSpec}
import model.core.result.SuccessType
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object WebGraphQLModels extends ToolGraphQLModelBasics[WebExerciseContent, WebSolution, WebExPart] {

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
    implicit val eft: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    deriveObjectType()
  }

  override val ExContentTypeType: ObjectType[Unit, WebExerciseContent] = {
    implicit val siteSpecT: ObjectType[Unit, SiteSpec] = siteSpecType

    implicit val eft: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    implicit val sampleSolType: ObjectType[Unit, SampleSolution[WebSolution]] =
      sampleSolutionType("Web", webSolutionType)

    deriveObjectType()
  }

  // Solution types

  override val SolTypeInputType: InputType[WebSolution] = {
    implicit val eft: InputObjectType[ExerciseFile] = ExerciseFileInputType

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
    implicit val gjart: ObjectType[Unit, GradedJsActionResult]            = gradedJsActionResultType

    deriveObjectType(
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
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
