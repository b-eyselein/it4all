package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.SiteSpec
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{AddFields, ExcludeFields, deriveObjectType}
import sangria.schema.{Field, IntType, ListType, ObjectType}

object WebGraphQLModels extends ToolGraphQLModelBasics[WebExerciseContent] {

  private val siteSpecType: ObjectType[Unit, SiteSpec] = {
    deriveObjectType(
      // TODO: include fields!?!
      ExcludeFields("htmlTasks", "jsTasks"),
      AddFields(
        Field("htmlTaskCount", IntType, resolve = _.value.htmlTasks.size),
        Field("jsTaskCount", IntType, resolve = _.value.jsTasks.size)
      )
    )
  }

  override val ExContentTypeType: ObjectType[Unit, WebExerciseContent] = {
    implicit val siteSpecT: ObjectType[Unit, SiteSpec] = siteSpecType

    implicit val eft: ObjectType[Unit, ExerciseFile] = exerciseFileType

    implicit val sampleSolType: ObjectType[Unit, SampleSolution[Seq[ExerciseFile]]] = sampleSolutionType(
      ListType(exerciseFileType)
    )

    deriveObjectType()
  }

}
