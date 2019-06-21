package model.tools.regex

import model.core.ToolForms
import model.tools.regex.RegexConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RegexToolForm extends ToolForms[RegexExercise, RegexCollection, RegexExerciseReview] {

  // Test data

  private val matchTestDataMapping: Mapping[RegexMatchTestData] = mapping(
    idName -> number,
    dataName -> nonEmptyText,
    includedName -> boolean
  )(RegexMatchTestData.apply)(RegexMatchTestData.unapply)

  private val extractionTestDataMapping: Mapping[RegexExtractionTestData] = mapping(
    idName -> number,
    "base" -> nonEmptyText
  )(RegexExtractionTestData.apply)(RegexExtractionTestData.unapply)

  // Complete exercise

  override val collectionFormat: Form[RegexCollection] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(RegexCollection.apply)(RegexCollection.unapply)
  )


  override val exerciseFormat    : Form[RegexExercise]       = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      maxPointsName -> number,
      correctionTypeName -> RegexCorrectionTypes.formField,
      samplesName -> seq(stringSampleMapping),
      matchTestDataName -> seq(matchTestDataMapping),
      extractionTestDataName -> seq(extractionTestDataMapping)
    )(RegexExercise.apply)(RegexExercise.unapply)
  )
  override val exerciseReviewForm: Form[RegexExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(RegexExerciseReview.apply)(RegexExerciseReview.unapply)
  )
}
