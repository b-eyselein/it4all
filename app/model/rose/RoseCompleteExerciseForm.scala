package model.rose

import model.core.CompleteExerciseForm
import model.programming.{ProgDataTypes, ProgLanguage, ProgLanguages}
import model.rose.RoseConsts._
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.data.{Form, Mapping}
import play.api.data.Forms._

object RoseCompleteExerciseForm extends CompleteExerciseForm[RoseCompleteEx] {

  // Input types

  final case class RoseInputTypeFormValues(id: Int, name: String, inputTypeStr: String)

  private val roseInputTypeMapping: Mapping[RoseInputTypeFormValues] = mapping(
    idName -> number,
    nameName -> nonEmptyText,
    inputTypeName -> nonEmptyText
  )(RoseInputTypeFormValues.apply)(RoseInputTypeFormValues.unapply)

  private def applyRoseInputType(fvs: RoseInputTypeFormValues, exerciseId: Int, exSemVer: SemanticVersion): RoseInputType =
    RoseInputType(fvs.id, exerciseId, exSemVer, fvs.name, ProgDataTypes.byName(fvs.inputTypeStr) getOrElse ProgDataTypes.VOID)

  private def unapplyRoseInputType(r: RoseInputType): RoseInputTypeFormValues =
    RoseInputTypeFormValues(r.id, r.name, r.inputType.typeName)


  // RoseSampleSolution

  final case class RoseSampleSolutionFormValues(id: Int, language: ProgLanguage, sample: String)

  private val roseSampleSolutionMapping: Mapping[RoseSampleSolutionFormValues] = mapping(
    idName -> number,
    languageName -> ProgLanguages.formField,
    sampleSolutionName -> nonEmptyText
  )(RoseSampleSolutionFormValues.apply)(RoseSampleSolutionFormValues.unapply)

  private def applyRoseSampleSolution(fvs: RoseSampleSolutionFormValues, exerciseId: Int, semanticVersion: SemanticVersion): RoseSampleSolution =
    RoseSampleSolution(fvs.id, exerciseId, semanticVersion, fvs.language, fvs.sample)

  private def unapplyRoseSampleSolution(r: RoseSampleSolution): RoseSampleSolutionFormValues =
    RoseSampleSolutionFormValues(r.id, r.language, r.solution)

  // Complete exericse

  override type FormData = (Int, SemanticVersion, String, String, String, ExerciseState, Int, Int, Boolean,
    Seq[RoseInputTypeFormValues], Seq[RoseSampleSolutionFormValues])

  def applyCompEx(id: Int, semanticVersion: SemanticVersion, title: String, author: String, exText: String,
                  state: ExerciseState, fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean,
                  roseInputTypeFormValues: Seq[RoseInputTypeFormValues],
                  roseSampleSolutionFormValues: Seq[RoseSampleSolutionFormValues]): RoseCompleteEx =
    RoseCompleteEx(
      RoseExercise(id, semanticVersion, title, author, exText, state, fieldWidth, fieldHeight, isMultiplayer),
      inputTypes = roseInputTypeFormValues map (r => applyRoseInputType(r, id, semanticVersion)),
      sampleSolutions = roseSampleSolutionFormValues map (r => applyRoseSampleSolution(r, id, semanticVersion))
    )

  override def unapplyCompEx(compEx: RoseCompleteEx): Option[FormData] =
    Some(
      (compEx.id, compEx.semanticVersion, compEx.title, compEx.author, compEx.text, compEx.state, compEx.fieldWidth, compEx.fieldHeight, compEx.isMultiplayer,
        compEx.inputTypes map unapplyRoseInputType,
        compEx.sampleSolutions map unapplyRoseSampleSolution)
    )

  override val format: Form[RoseCompleteEx] = Form(
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
    )(applyCompEx)(unapplyCompEx)
  )

}
