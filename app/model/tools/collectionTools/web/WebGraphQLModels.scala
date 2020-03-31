package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import model.points
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{AddFields, ExcludeFields, deriveObjectType}
import sangria.schema._

object WebGraphQLModels extends ToolGraphQLModelBasics[WebExerciseContent, Seq[ExerciseFile], WebExPart] {

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

  override val ExContentTypeType: ObjectType[Unit, WebExerciseContent] = {
    implicit val siteSpecT: ObjectType[Unit, SiteSpec] = siteSpecType

    implicit val eft: ObjectType[Unit, ExerciseFile] = ExerciseFileType

    implicit val sampleSolType: ObjectType[Unit, SampleSolution[Seq[ExerciseFile]]] =
      sampleSolutionType("Web", ListType(ExerciseFileType))

    deriveObjectType()
  }

  // Solution types

  override val SolTypeInputType: InputType[Seq[ExerciseFile]] = ListInputType(ExerciseFileInputType)

  // Result types

  override val AbstractResultTypeType: OutputType[Any] = {
    implicit val pt: ObjectType[Unit, points.Points] = pointsType

    deriveObjectType[Unit, WebCompleteResult](
      // TODO: include fields!
      ExcludeFields("gradedHtmlTaskResults", "gradedJsTaskResults")
    )
  }

  override val PartTypeInputType: EnumType[WebExPart] = EnumType(
    "WebExPart",
    values = WebExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
