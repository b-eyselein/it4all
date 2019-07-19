package model.tools.programming

import model.core.ToolForms
import model.tools.programming.ProgConsts._
import model.tools.uml.UmlToolForms.classDiagramMapping
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}
import play.api.libs.json.{JsValue, Json}

object ProgToolForms extends ToolForms[ProgExercise, ProgCollection, ProgExerciseReview] {

  private val jsValueFormMapping: Mapping[JsValue] = nonEmptyText.transform(Json.parse, Json.prettyPrint)

  private val progSolutionMapping: Mapping[ProgSolution] = mapping(
    filesName -> seq(exerciseFileMapping)
  )(ProgSolution.apply(_, Seq.empty))(ProgSolution.unapply(_).map(_._1))

  private val programmingSampleSolutionMapping: Mapping[ProgSampleSolution] = mapping(
    idName -> number,
    solutionName -> progSolutionMapping
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

  private val unitTestPartMapping: Mapping[UnitTestPart] = mapping(
    unitTestTypeName -> UnitTestTypes.formField,
    unitTestsDescriptionName -> nonEmptyText,
    unitTestFilesName -> seq(exerciseFileMapping),
    unitTestTestConfigsName -> seq(unitTestTestConfigMapping),
    testFileNameName -> nonEmptyText,
    sampleSolFilesNamesName -> seq(nonEmptyText)
  )(UnitTestPart.apply)(UnitTestPart.unapply)

  private val implementationPartMapping: Mapping[ImplementationPart] = mapping(
    baseName -> nonEmptyText,
    filesName -> seq(exerciseFileMapping),
    implFileNameName -> nonEmptyText,
    sampleSolFilesNamesName -> seq(nonEmptyText)
  )(ImplementationPart.apply)(ImplementationPart.unapply)

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
      foldernameName -> nonEmptyText,
      filenameName -> nonEmptyText,

      inputTypesName -> seq(progInputMapping),
      outputTypeName -> ProgDataTypes.formField,

      baseDataName -> optional(jsValueFormMapping),

      unitTestPartName -> unitTestPartMapping,
      implementationPartName -> implementationPartMapping,

      sampleSolutionsName -> seq(programmingSampleSolutionMapping),
      sampleTestDataName -> seq(programmingSampleTestDataMapping),

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
