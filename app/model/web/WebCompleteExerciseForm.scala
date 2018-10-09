package model.web

import model.core.CompleteExerciseForm
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import model.web.WebConsts._

object WebCompleteExerciseForm extends CompleteExerciseForm[WebExercise, WebCompleteEx] {

  // HtmlCompleteTask(task: HtmlTask, attributes: Seq[Attribute])

  case class HtmlCompleteTaskFormValues(taskId: Int, taskText: String, xpathQuery: String, textContent: Option[String])

  private def applyHtmlTask(exerciseId: Int, exSemVer: SemanticVersion): HtmlCompleteTaskFormValues => HtmlCompleteTask = {
    case HtmlCompleteTaskFormValues(taskId, taskText, xpathQuery, textContent) =>
      HtmlCompleteTask(
        HtmlTask(taskId, exerciseId, exSemVer, taskText, xpathQuery, textContent),
        attributes = Seq[Attribute]() // TODO!
      )
  }

  private def unapplyHtmlTask(t: HtmlCompleteTask): HtmlCompleteTaskFormValues =
    HtmlCompleteTaskFormValues(t.task.id, t.task.text, t.task.xpathQuery, t.task.textContent)

  private val htmlTasksMapping: Mapping[HtmlCompleteTaskFormValues] = mapping(
    idName -> number,
    textName -> nonEmptyText,
    xpathQueryName -> nonEmptyText,
    textContentName -> optional(nonEmptyText)
  )(HtmlCompleteTaskFormValues.apply)(HtmlCompleteTaskFormValues.unapply)

  // JsCompleteTask(task: JsTask, conditions: Seq[JsCondition])

  case class JsCompleteTaskFormValues(taskId: Int, taskText: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String])

  private def applyJstask(exerciseId: Int, exSemVer: SemanticVersion): JsCompleteTaskFormValues => JsCompleteTask = {
    case JsCompleteTaskFormValues(taskId, taskText, xpathQuery, actionType, keysToSend) =>
      JsCompleteTask(
        JsTask(taskId, exerciseId, exSemVer, taskText, xpathQuery, actionType, keysToSend),
        conditions = Seq[JsCondition]() // TODO!
      )
  }

  private def unapplyJsTask(t: JsCompleteTask): JsCompleteTaskFormValues =
    JsCompleteTaskFormValues(t.task.id, t.task.text, t.task.xpathQuery, t.task.actionType, t.task.keysToSend)

  private val jsTasksMapping = mapping(
    idName -> number,
    textName -> nonEmptyText,
    xpathQueryName -> nonEmptyText,
    actionTypeName -> JsActionType.formField,
    keysToSendName -> optional(nonEmptyText)
  )(JsCompleteTaskFormValues.apply)(JsCompleteTaskFormValues.unapply)

  // WebSampleSolution

  final case class WebSampleSolutionFormValues(id: Int, htmlSample: Option[String], jsSample: Option[String], phpSample: Option[String])

  private def applyWebSampleSolution(exId: Int, exSemVer: SemanticVersion): WebSampleSolutionFormValues => WebSampleSolution = {
    case WebSampleSolutionFormValues(id, htmlSample, jsSample, phpSample) => WebSampleSolution(id, exId, exSemVer, htmlSample, jsSample, phpSample)
  }

  private def unapplyWebSampleSolution(t: WebSampleSolution): WebSampleSolutionFormValues =
    WebSampleSolutionFormValues(t.id, t.htmlSample, t.jsSample, t.phpSample)

  private val webSampleSolutionMapping: Mapping[WebSampleSolutionFormValues] = mapping(
    idName -> number,
    "htmlSample" -> optional(nonEmptyText),
    "jsSample" -> optional(nonEmptyText),
    "phpSample" -> optional(nonEmptyText)
  )(WebSampleSolutionFormValues.apply)(WebSampleSolutionFormValues.unapply)

  // Complete exercise

  override type FormData = (Int, SemanticVersion, String, String, String, ExerciseState, Option[String], Option[String], Option[String],
    Seq[HtmlCompleteTaskFormValues], Seq[JsCompleteTaskFormValues], Seq[WebSampleSolutionFormValues])

  private def applyCompEx(id: Int, semVer: SemanticVersion, author: String, title: String, exText: String, status: ExerciseState,
                          htmlText: Option[String], jsText: Option[String], phpText: Option[String],
                          htmlCompleteTaskFormValues: Seq[HtmlCompleteTaskFormValues],
                          jsCompleteTaskFormValues: Seq[JsCompleteTaskFormValues],
                          webSampleFormValue: Seq[WebSampleSolutionFormValues]): WebCompleteEx =
    WebCompleteEx(
      WebExercise(id, semVer, author, title, exText, status, htmlText, jsText, phpText),
      htmlTasks = htmlCompleteTaskFormValues map applyHtmlTask(id, semVer),
      jsTasks = jsCompleteTaskFormValues map applyJstask(id, semVer),
      sampleSolutions = webSampleFormValue map applyWebSampleSolution(id, semVer)
    )

  override protected def unapplyCompEx(compEx: WebCompleteEx): Option[FormData] = Some((
    compEx.ex.id, compEx.ex.semanticVersion, compEx.ex.title, compEx.ex.author, compEx.ex.text, compEx.ex.state,
    compEx.ex.htmlText, compEx.ex.jsText, compEx.ex.phpText,
    compEx.htmlTasks map unapplyHtmlTask, compEx.jsTasks map unapplyJsTask,
    compEx.sampleSolutions map unapplyWebSampleSolution
  ))

  override val format: Form[WebCompleteEx] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,

      htmlTextName -> optional(nonEmptyText),
      jsTextName -> optional(nonEmptyText),
      phpTextName -> optional(nonEmptyText),

      htmlTasksName -> seq(htmlTasksMapping),
      jsTasksName -> seq(jsTasksMapping),

      samplesName -> seq(webSampleSolutionMapping)
    )(applyCompEx)(unapplyCompEx)
  )

}