package model.tools.sql

import model.core.ExerciseForm
import model.tools.sql.SqlConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object SqlFormMappings extends ExerciseForm[SqlExercise, SqlScenario, SqlExerciseReview] {

  private val sqlSampleMapping: Mapping[SqlSampleSolution] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(SqlSampleSolution.apply)(SqlSampleSolution.unapply)

  // Complete exercise

  override val collectionFormat: Form[SqlScenario] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(SqlScenario.apply)(SqlScenario.unapply)
  )

  override val exerciseFormat: Form[SqlExercise] = Form(
    mapping(
      idName -> number,
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
