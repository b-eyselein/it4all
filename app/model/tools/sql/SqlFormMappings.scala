package model.tools.sql

import model.core.ExerciseForm
import model.tools.sql.SqlConsts._
import model.{ExerciseState, SemanticVersionHelper}
import play.api.data.{Form, Mapping}
import play.api.data.Forms._

object SqlFormMappings extends ExerciseForm[SqlExercise, SqlScenario] {

  private val sqlSampleMapping: Mapping[SqlSampleSolution] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(SqlSampleSolution.apply)(SqlSampleSolution.unapply)

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
}
