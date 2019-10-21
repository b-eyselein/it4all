package model.core

import model._
import model.core.CoreConsts._
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

trait ToolForms[ExType <: Exercise, ReviewType <: ExerciseReview] {

  val collectionFormat: Form[ExerciseCollection] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(ExerciseCollection.apply)(ExerciseCollection.unapply)
  )

  val exerciseFormat: Form[ExType]

  val exerciseReviewForm: Form[ReviewType]

  protected val stringSampleMapping: Mapping[StringSampleSolution] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(StringSampleSolution.apply)(StringSampleSolution.unapply)

  protected val exerciseFileMapping: Mapping[ExerciseFile] = mapping(
    pathName -> nonEmptyText,
    "resourcePath" -> nonEmptyText,
    "fileType" -> nonEmptyText,
    "editable" -> boolean
  )(ExerciseFile.apply)(ExerciseFile.unapply)

}
