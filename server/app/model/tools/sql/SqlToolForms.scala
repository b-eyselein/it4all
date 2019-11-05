package model.tools.sql

import model.core.ToolForms
import model.tools.sql.SqlConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper, StringSampleSolution}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object SqlToolForms extends ToolForms[SqlExercise, SqlExerciseReview] {

  private val sqlSampleMapping: Mapping[StringSampleSolution] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(StringSampleSolution.apply)(StringSampleSolution.unapply)

  // Complete exercise

  override val exerciseFormat: Form[SqlExercise] = Form(
    mapping(
      idName -> number,
      collectionIdName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      exerciseTypeName -> SqlExerciseType.formField,
      tagsName -> seq(SqlExTag.formField),
      hintName -> optional(text),
      samplesName -> seq(sqlSampleMapping)
    )(SqlExercise.apply)(SqlExercise.unapply)
  )

  override val exerciseReviewForm: Form[SqlExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(SqlExerciseReview.apply)(SqlExerciseReview.unapply)
  )

}
