package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import model.points
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

  private val webCompleteResultType: ObjectType[Unit, WebCompleteResult] = {
    implicit val pt: ObjectType[Unit, points.Points] = pointsType

    deriveObjectType(
      Interfaces(abstractResultTypeType),
      ExcludeFields( /*"solutionSaved",*/ "points", "maxPoints"),
      // TODO: include fields!
      ExcludeFields("gradedHtmlTaskResults", "gradedJsTaskResults")
    )
  }

  override val AbstractResultTypeType: OutputType[Any] = webCompleteResultType

  override val PartTypeInputType: EnumType[WebExPart] = EnumType(
    "WebExPart",
    values = WebExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
