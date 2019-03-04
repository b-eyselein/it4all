package model.tools.rose

import model.core.ToolForms
import model.tools.programming.{ProgDataType, ProgDataTypes, ProgLanguages}
import model.tools.rose.RoseConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RoseToolForms extends ToolForms[RoseExercise, RoseCollection, RoseExerciseReview] {

  // Input types

  private val roseInputTypeMapping: Mapping[RoseInputType] = mapping(
    idName -> number,
    nameName -> nonEmptyText,
    inputTypeName -> nonEmptyText.transform[ProgDataType](ProgDataTypes.byName(_).getOrElse(???), _.typeName)
  )(RoseInputType.apply)(RoseInputType.unapply)

  // RoseSampleSolution

  private val roseSampleSolutionMapping: Mapping[RoseSampleSolution] = mapping(
    idName -> number,
    languageName -> ProgLanguages.formField,
    sampleSolutionName -> nonEmptyText
  )(RoseSampleSolution.apply)(RoseSampleSolution.unapply)

  // Complete exericse

  override val collectionFormat: Form[RoseCollection] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(RoseCollection.apply)(RoseCollection.unapply)
  )

  override val exerciseFormat: Form[RoseExercise] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      fieldWidthName -> number,
      fieldHeightName -> number,
      isMultiplayerName -> boolean,
      inputTypesName -> seq(roseInputTypeMapping),
      sampleSolutionName -> seq(roseSampleSolutionMapping)
    )(RoseExercise.apply)(RoseExercise.unapply)
  )

  override val exerciseReviewForm: Form[RoseExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(RoseExerciseReview.apply)(RoseExerciseReview.unapply)
  )

}
