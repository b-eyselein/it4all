package model.tools.web

import model.graphql.{FilesSolutionToolGraphQLModelBasics, GraphQLArguments}
import model.result.SuccessType
import model.tools.web.sitespec.{HtmlTask, JsAction, JsActionType, SiteSpec}
import model.{ExerciseFile, FilesSolution}
import sangria.macros.derive._
import sangria.schema._

object WebGraphQLModels extends FilesSolutionToolGraphQLModelBasics[WebExerciseContent, WebExPart, WebResult] with GraphQLArguments {

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

  override val exerciseContentType: ObjectType[Unit, WebExerciseContent] = {
    implicit val siteSpecT: ObjectType[Unit, SiteSpec] = siteSpecType
    implicit val eft: ObjectType[Unit, ExerciseFile]   = exerciseFileType
    implicit val sst: ObjectType[Unit, FilesSolution]  = solutionOutputType

    deriveObjectType(
      AddFields(
        Field(
          "part",
          OptionType(partEnumType),
          arguments = partIdArgument :: Nil,
          resolve = context => WebExPart.values.find(_.id == context.arg(partIdArgument))
        )
      )
    )
  }

  // Result types

  private val gradedTextResultType: ObjectType[Unit, GradedTextResult] = deriveObjectType(
    ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
    ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
  )

  implicit val gradedJsActionResultType: ObjectType[Unit, GradedJsActionResult] = {
    implicit val jat: ObjectType[Unit, JsAction] = jsActionType

    deriveObjectType(
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

  private val gradedElementSpecResultInterfaceType: InterfaceType[Unit, GradedElementSpecResult] = InterfaceType(
    "GradedElementSpecResult",
    fields[Unit, GradedElementSpecResult](
      Field("id", IntType, resolve = _.value.id),
      Field("success", successTypeType, resolve = _.value.success),
      Field("elementFound", BooleanType, resolve = _.value.elementFound),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble),
      Field("textContentResult", OptionType(gradedTextResultType), resolve = _.value.textContentResult),
      Field("attributeResults", ListType(gradedTextResultType), resolve = _.value.attributeResults)
    )
  )

  private val gradedHtmlTaskResultType: ObjectType[Unit, GradedHtmlTaskResult] = {
    implicit val stt: EnumType[SuccessType]               = successTypeType
    implicit val gtrt: ObjectType[Unit, GradedTextResult] = gradedTextResultType

    deriveObjectType(
      Interfaces(gradedElementSpecResultInterfaceType),
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

  private val gradedJsHtmlElementSpecResultType: ObjectType[Unit, GradedJsHtmlElementSpecResult] = {
    implicit val stt: EnumType[SuccessType]               = successTypeType
    implicit val gtrt: ObjectType[Unit, GradedTextResult] = gradedTextResultType

    deriveObjectType(
      Interfaces(gradedElementSpecResultInterfaceType),
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

  override val resultType: OutputType[WebResult] = {
    implicit val ghtrt: ObjectType[Unit, GradedHtmlTaskResult] = gradedHtmlTaskResultType
    implicit val gjtrt: ObjectType[Unit, GradedJsTaskResult]   = gradedJsTaskResultType

    deriveObjectType[Unit, WebResult](
      ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
      ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
    )
  }

}
