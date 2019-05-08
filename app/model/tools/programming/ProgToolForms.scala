package model.tools.programming

import model.core.ToolForms
import model.tools.programming.ProgConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import model.tools.uml.UmlToolForms.classDiagramMapping
import play.api.data.{Form, Mapping}
import play.api.libs.json.{JsValue, Json}

object ProgToolForms extends ToolForms[ProgExercise, ProgCollection, ProgExerciseReview] {

  private val jsValueFormMapping: Mapping[JsValue] = nonEmptyText.transform(Json.parse, Json.prettyPrint)

  private val programmingSampleSolutionMapping: Mapping[ProgSampleSolution] = mapping(
    idName -> number,
    //    languageName -> ProgLanguages.formField,
    baseName -> nonEmptyText,
    solutionName -> nonEmptyText
  )(ProgSampleSolution.apply)(ProgSampleSolution.unapply)

  val programmingSampleTestDataMapping: Mapping[ProgSampleTestData] = mapping(
    idName -> number,
    inputsName -> jsValueFormMapping,
    outputName -> jsValueFormMapping
  )(ProgSampleTestData.apply)(ProgSampleTestData.unapply)

  val progInputMapping: Mapping[ProgInput] = mapping(
    idName -> number,
    nameName -> nonEmptyText,
    inputTypeName -> ProgDataTypes.formField
  )(ProgInput.apply)(ProgInput.unapply)

  private val unitTestTestConfigMapping: Mapping[UnitTestTestConfig] = mapping(
    idName -> number,
    "shouldFail" -> boolean,
    "cause" -> optional(nonEmptyText),
    descriptionName -> nonEmptyText
  )(UnitTestTestConfig.apply)(UnitTestTestConfig.unapply)

  // Complete exercise

  override val collectionFormat: Form[ProgCollection] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(ProgCollection.apply)(ProgCollection.unapply)
  )

  override val exerciseFormat: Form[ProgExercise] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      functionNameName -> nonEmptyText,
      outputTypeName -> ProgDataTypes.formField,
      baseDataName -> optional(jsValueFormMapping),
      unitTestTypeName -> UnitTestTypes.formField,
      inputTypesName -> seq(progInputMapping),
      sampleSolutionsName -> seq(programmingSampleSolutionMapping),
      sampleTestDataName -> seq(programmingSampleTestDataMapping),
      unitTestsDescriptionName -> nonEmptyText,
      unitTestFilesName -> seq(exerciseFileMapping),
      foldernameName -> nonEmptyText,
      filenameName -> nonEmptyText,
      unitTestTestConfigsName -> seq(unitTestTestConfigMapping),
      "maybeClassDiagram" -> optional(classDiagramMapping)
    )(ProgExercise.apply)(ProgExercise.unapply)
  )

  override val exerciseReviewForm: Form[ProgExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(ProgExerciseReview.apply)(ProgExerciseReview.unapply)
  )

}
