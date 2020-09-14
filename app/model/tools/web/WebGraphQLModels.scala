package model.tools.web

import de.uniwue.webtester.sitespec.{HtmlTask, JsAction, JsActionType, SiteSpec}
import model.graphql.{FilesSolutionToolGraphQLModelBasics, GraphQLArguments}
import model.result.SuccessType
import model.{ExerciseFile, FilesSolution, SampleSolution}
import sangria.macros.derive._
import sangria.schema._

object WebGraphQLModels
    extends FilesSolutionToolGraphQLModelBasics[WebExerciseContent, WebExPart, WebAbstractResult]
    with GraphQLArguments {

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
    implicit val siteSpecT: ObjectType[Unit, SiteSpec]                = siteSpecType
    implicit val eft: ObjectType[Unit, ExerciseFile]                  = exerciseFileType
    implicit val sst: ObjectType[Unit, SampleSolution[FilesSolution]] = sampleSolutionType

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

// Abstract result

  private val webAbstractResultType: InterfaceType[Unit, WebAbstractResult] = InterfaceType(
    "WebAbstractResult",
    fields[Unit, WebAbstractResult](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    ),
    interfaces[Unit, WebAbstractResult](abstractResultInterfaceType)
  ).withPossibleTypes(() => List(webResultType, webInternalErrorResultType))

  private val webInternalErrorResultType: ObjectType[Unit, WebInternalErrorResult] = deriveObjectType(
    Interfaces(webAbstractResultType),
    ExcludeFields("maxPoints")
  )

  private val webResultType: ObjectType[Unit, WebResult] = {
    implicit val ghtrt: ObjectType[Unit, GradedHtmlTaskResult] = gradedHtmlTaskResultType
    implicit val gjtrt: ObjectType[Unit, GradedJsTaskResult]   = gradedJsTaskResultType

    deriveObjectType(
      Interfaces(webAbstractResultType),
      ExcludeFields("points", "maxPoints")
    )
  }

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, WebAbstractResult] = webAbstractResultType

}
