package model.core

import model._
import model.core.CoreConsts._
import model.tools.collectionTools.{Exercise, ExerciseCollection, ExerciseFile}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

trait ToolForms[ExType <: Exercise, ReviewType <: ExerciseReview] {

  def collectionFormat(toolId: String): Form[ExerciseCollection] = {
    val unapplied: ExerciseCollection => Option[(Int, String, String, String, ExerciseState, String)] = {
      case ExerciseCollection(id, _, title, author, text, state, shortName) => Some((id, title, author, text, state, shortName))
    }

    Form(
      mapping(
        idName -> number,
        titleName -> nonEmptyText,
        authorName -> nonEmptyText,
        textName -> nonEmptyText,
        statusName -> ExerciseState.formField,
        shortNameName -> nonEmptyText
      )(ExerciseCollection.apply(_, toolId, _, _, _, _, _))(unapplied)
    )
  }

  val exerciseReviewForm: Form[ReviewType]

  protected val stringSampleMapping: Mapping[StringSampleSolution] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(StringSampleSolution.apply)(StringSampleSolution.unapply)

  protected val exerciseFileMapping: Mapping[ExerciseFile] = mapping(
    "path" -> nonEmptyText,
    "resourcePath" -> nonEmptyText,
    "fileType" -> nonEmptyText,
    "editable" -> boolean
  )(ExerciseFile.apply)(ExerciseFile.unapply)

}
