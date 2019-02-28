package model.tools.regex

import model.core.ExerciseForm
import model.tools.regex.RegexConsts._
import model.{ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RegexExForm extends ExerciseForm[RegexExercise, RegexCollection] {

  // Sample solutions

  private val sampleMapping: Mapping[RegexSampleSolution] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(RegexSampleSolution.apply)(RegexSampleSolution.unapply)

  // Test data

  private val testDataMapping: Mapping[RegexTestData] = mapping(
    idName -> number,
    dataName -> nonEmptyText,
    includedName -> boolean
  )(RegexTestData.apply)(RegexTestData.unapply)

  // Complete exercise

  override val exerciseFormat: Form[RegexExercise] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      samplesName -> seq(sampleMapping),
      testDataName -> seq(testDataMapping)
    )(RegexExercise.apply)(RegexExercise.unapply)
  )

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

}
