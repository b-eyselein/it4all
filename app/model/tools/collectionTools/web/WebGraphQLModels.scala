package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.SiteSpec
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, deriveObjectType}
import sangria.schema.{ListType, ObjectType}

object WebGraphQLModels extends ToolGraphQLModelBasics[WebExerciseContent] {

  private val siteSpecType: ObjectType[Unit, SiteSpec] = {
    deriveObjectType(
      // TODO: include fields!?!
      ExcludeFields("htmlTasks", "jsTasks")
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
