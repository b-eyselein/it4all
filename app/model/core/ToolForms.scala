package model.core

import model.tools.regex.RegexConsts.{idName, sampleName}
import model.tools.web.WebConsts.pathName
import model.{Exercise, ExerciseCollection, ExerciseFile, ExerciseReview, StringSampleSolution}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

trait ToolForms[ExType <: Exercise, CollType <: ExerciseCollection, ReviewType <: ExerciseReview] {

  val collectionFormat: Form[CollType]

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
