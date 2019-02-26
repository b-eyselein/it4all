package model.tools.regex

import model.core.ExerciseForm
import model.tools.regex.RegexConsts._
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RegexExForm extends ExerciseForm[RegexExercise] {

  // Sample solutions

  final case class RegexSampleSolutionFormValues(id: Int, sample: String)

  private def applyRegexSample(exId: Int, exSemVer: SemanticVersion, collId: Int, rssfv: RegexSampleSolutionFormValues): RegexSampleSolution =
    RegexSampleSolution(rssfv.id, exId, exSemVer, collId, rssfv.sample)

  private def unapplyRegexSampleSolution(rss: RegexSampleSolution): RegexSampleSolutionFormValues =
    RegexSampleSolutionFormValues(rss.id, rss.sample)

  private val sampleMapping: Mapping[RegexSampleSolutionFormValues] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(RegexSampleSolutionFormValues.apply)(RegexSampleSolutionFormValues.unapply)

  // Test data

  final case class RegexTestDataFormValues(id: Int, data: String, isIncluded: Boolean)

  private def applyRegexTestDataFormValues(exId: Int, exSemVer: SemanticVersion, collId: Int, rtdfv: RegexTestDataFormValues): RegexTestData =
    RegexTestData(rtdfv.id, exId, exSemVer, collId, rtdfv.data, rtdfv.isIncluded)

  private def unapplyRegexTestData(rtd: RegexTestData): RegexTestDataFormValues =
    RegexTestDataFormValues(rtd.id, rtd.data, rtd.isIncluded)

  private val testDataMapping: Mapping[RegexTestDataFormValues] = mapping(
    idName -> number,
    dataName -> nonEmptyText,
    includedName -> boolean
  )(RegexTestDataFormValues.apply)(RegexTestDataFormValues.unapply)

  // Complete exercise

  // TODO: update...
  override val format: Form[RegexExercise] = null

  // TODO: update...
  override protected def unapplyCompEx(compEx: RegexExercise): Option[RegexExForm.FormData] = ???

  //  override type FormData = (Int, SemanticVersion, String, String, String, ExerciseState,
  //    Seq[RegexSampleSolutionFormValues], Seq[RegexTestDataFormValues])
  //
  //  override val format: Form[RegexExercise] = Form(
  //    mapping(
  //      idName -> number,
  //      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
  //      titleName -> nonEmptyText,
  //      authorName -> nonEmptyText,
  //      textName -> nonEmptyText,
  //      statusName -> ExerciseState.formField,
  //      samplesName -> seq(sampleMapping),
  //      testDataName -> seq(testDataMapping)
  //    )(applyCompEx)(unapplyCompEx)
  //  )
  //
  //  override protected def unapplyCompEx(compEx: RegexExercise): Option[FormData] = Some((
  //    compEx.id, compEx.semanticVersion, compEx.title, compEx.author, compEx.text, compEx.state,
  //    compEx.sampleSolutions.map(unapplyRegexSampleSolution), compEx.testData.map(unapplyRegexTestData)
  //  ))
  //
  //  def applyCompEx(id: Int, semanticVersion: SemanticVersion, collId: Int, title: String, author: String, text: String, state: ExerciseState,
  //                  sampleSolutionFormValues: Seq[RegexSampleSolutionFormValues], testDataFormValues: Seq[RegexTestDataFormValues]): RegexExercise =
  //    RegexExercise(
  //      id, semanticVersion, title, author, text, state,
  //      sampleSolutions = sampleSolutionFormValues.map(applyRegexSample(id, semanticVersion, collId, _)),
  //      testData = testDataFormValues.map(applyRegexTestDataFormValues(id, semanticVersion, collId, _))
  //    )

}
