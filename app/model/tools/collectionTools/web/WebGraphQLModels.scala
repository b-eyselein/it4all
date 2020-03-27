package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.{HtmlTask, SiteSpec}
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{AddFields, ExcludeFields, deriveObjectType}
import sangria.schema.{Field, InputType, IntType, ListInputType, ListType, ObjectType, StringType, fields}

object WebGraphQLModels extends ToolGraphQLModelBasics[WebExerciseContent, Seq[ExerciseFile]] {

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

  override val SolTypeInputType: InputType[Seq[ExerciseFile]] = ListInputType(ExerciseFileInputType)

}
