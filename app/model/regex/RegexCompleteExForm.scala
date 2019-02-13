package model.regex

import model.regex.RegexConsts._
import model.core.CompleteExerciseForm
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object RegexCompleteExForm extends CompleteExerciseForm[ RegexCompleteEx] {

  // Sample solutions

  final case class RegexSampleSolutionFormValues(id: Int, sample: String)

  private def applyRegexSample(exId: Int, exSemVer: SemanticVersion, rssfv: RegexSampleSolutionFormValues): RegexSampleSolution =
    RegexSampleSolution(rssfv.id, exId, exSemVer, rssfv.sample)

  private def unapplyRegexSampleSolution(rss: RegexSampleSolution): RegexSampleSolutionFormValues =
    RegexSampleSolutionFormValues(rss.id, rss.sample)

  private val sampleMapping: Mapping[RegexSampleSolutionFormValues] = mapping(
    idName -> number,
    sampleName -> nonEmptyText
  )(RegexSampleSolutionFormValues.apply)(RegexSampleSolutionFormValues.unapply)

  // Test data

  final case class RegexTestDataFormValues(id: Int, data: String, isIncluded: Boolean)

  private def applyRegexTestDataFormValues(exId: Int, exSemVer: SemanticVersion, rtdfv: RegexTestDataFormValues): RegexTestData =
    RegexTestData(rtdfv.id, exId, exSemVer, rtdfv.data, rtdfv.isIncluded)

  private def unapplyRegexTestData(rtd: RegexTestData): RegexTestDataFormValues =
    RegexTestDataFormValues(rtd.id, rtd.data, rtd.isIncluded)

  private val testDataMapping: Mapping[RegexTestDataFormValues] = mapping(
    idName -> number,
    dataName -> nonEmptyText,
    includedName -> boolean
  )(RegexTestDataFormValues.apply)(RegexTestDataFormValues.unapply)

  // Complete exercise

  override type FormData = (Int, SemanticVersion, String, String, String, ExerciseState,
    Seq[RegexSampleSolutionFormValues], Seq[RegexTestDataFormValues])

  override val format: Form[RegexCompleteEx] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      samplesName -> seq(sampleMapping),
      testDataName -> seq(testDataMapping)
    )(applyCompEx)(unapplyCompEx)
  )

  override protected def unapplyCompEx(compEx: RegexCompleteEx): Option[FormData] = Some((
    compEx.id, compEx.semanticVersion, compEx.title, compEx.author, compEx.text, compEx.state,
    compEx.sampleSolutions.map(unapplyRegexSampleSolution), compEx.testData.map(unapplyRegexTestData)
  ))

  def applyCompEx(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                  sampleSolutionFormValues: Seq[RegexSampleSolutionFormValues], testDataFormValues: Seq[RegexTestDataFormValues]): RegexCompleteEx =
    RegexCompleteEx(
      RegexExercise(id, title, author, text, state, semanticVersion),
      sampleSolutions = sampleSolutionFormValues.map(applyRegexSample(id, semanticVersion, _)),
      testData = testDataFormValues.map(applyRegexTestDataFormValues(id, semanticVersion, _))
    )

}
