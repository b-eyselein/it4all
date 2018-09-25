package model.web

import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.data.{Form, Mapping}
import play.api.data.Forms._
import model.web.WebConsts._

object WebCompleteExerciseForm {

  type FormData = (Int, SemanticVersion, String, String, String, ExerciseState, String, Option[String], Option[String], Option[String],
    Seq[HtmlCompleteTaskFormValues], Seq[JsCompleteTaskFormValues])

  // HtmlCompleteTask(task: HtmlTask, attributes: Seq[Attribute])

  case class HtmlCompleteTaskFormValues(taskId: Int, taskText: String, xpathQuery: String, textContent: Option[String])

  def applyHtmlTask(exerciseId: Int, exSemVer: SemanticVersion, fvs: HtmlCompleteTaskFormValues): HtmlCompleteTask = HtmlCompleteTask(
    HtmlTask(fvs.taskId, exerciseId, exSemVer, fvs.taskText, fvs.xpathQuery, fvs.textContent),
    attributes = Seq[Attribute]() // TODO!
  )

  def unapplyHtmlTask(htmlCompleteTask: HtmlCompleteTask): HtmlCompleteTaskFormValues = ???

  private val htmlTasksMapping: Mapping[HtmlCompleteTaskFormValues] = mapping(
    idName -> number,
    textName -> nonEmptyText,
    xpathQueryName -> nonEmptyText,
    textContentName -> optional(nonEmptyText)
  )(HtmlCompleteTaskFormValues.apply)(HtmlCompleteTaskFormValues.unapply)

  // JsCompleteTask(task: JsTask, conditions: Seq[JsCondition])

  case class JsCompleteTaskFormValues(taskId: Int, taskText: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String])

  def applyJstask(exerciseId: Int, exSemVer: SemanticVersion, fvs: JsCompleteTaskFormValues): JsCompleteTask = JsCompleteTask(
    JsTask(fvs.taskId, exerciseId, exSemVer, fvs.taskText, fvs.xpathQuery, fvs.actionType, fvs.keysToSend),
    conditions = Seq[JsCondition]() // TODO!
  )

  def unapplyJsTask(jsCompleteTask: JsCompleteTask): JsCompleteTaskFormValues = ???

  private val jsTasksMapping = mapping(
    idName -> number,
    textName -> nonEmptyText,
    xpathQueryName -> nonEmptyText,
    actionTypeName -> JsActionType.formField,
    keysToSendName -> optional(nonEmptyText)
  )(JsCompleteTaskFormValues.apply)(JsCompleteTaskFormValues.unapply)

  // Complete exercise

  val format: Form[WebCompleteEx] = {

    def apply(id: Int, semVer: SemanticVersion, author: String, title: String, exText: String, status: ExerciseState, sampleSol: String,
              htmlText: Option[String], jsText: Option[String], phpText: Option[String],
              htmlCompleteTaskFormValues: Seq[HtmlCompleteTaskFormValues],
              jsCompleteTaskFormValues: Seq[JsCompleteTaskFormValues]): WebCompleteEx =
      WebCompleteEx(
        WebExercise(id, semVer, author, title, exText, status, sampleSol, htmlText, jsText, phpText),
        htmlTasks = htmlCompleteTaskFormValues map (task => applyHtmlTask(id, semVer, task)),
        jsTasks = jsCompleteTaskFormValues map (task => applyJstask(id, semVer, task))
      )

    def unapply(compEx: WebCompleteEx): Option[FormData] = Some((
      compEx.ex.id, compEx.ex.semanticVersion, compEx.ex.title, compEx.ex.author, compEx.ex.text, compEx.ex.state,
      compEx.ex.sampleSolution, compEx.ex.htmlText, compEx.ex.jsText, compEx.ex.phpText,
      compEx.htmlTasks map unapplyHtmlTask, compEx.jsTasks map unapplyJsTask
    ))

    Form(
      mapping(
        idName -> number,
        semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
        titleName -> nonEmptyText,
        authorName -> nonEmptyText,
        textName -> nonEmptyText,
        statusName -> ExerciseState.formField,
        sampleSolutionName -> nonEmptyText,
        htmlTextName -> optional(nonEmptyText),
        jsTextName -> optional(nonEmptyText),
        phpTextName -> optional(nonEmptyText),
        htmlTasksName -> seq(htmlTasksMapping),
        jsTasksName -> seq(jsTasksMapping)
      )(apply)(unapply)
    )
  }

}
