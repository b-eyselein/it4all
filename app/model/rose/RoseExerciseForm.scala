package model.rose

import model.core.ExerciseForm
import model.programming.{ProgDataType, ProgDataTypes, ProgLanguages}
import model.rose.RoseConsts._
import model.{ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RoseExerciseForm extends ExerciseForm[RoseExercise, RoseCollection] {

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

}
