package model.tools.web

import de.uniwue.webtester._
import model.core.ToolForms
import model.tools.web.WebConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object WebToolForms extends ToolForms[WebExercise, WebCollection, WebExerciseReview] {

  // HtmlTask

  private val htmlAttributesMapping: Mapping[HtmlAttribute] = mapping(
    keyName -> nonEmptyText,
    valueName -> nonEmptyText
  )(HtmlAttribute.apply)(HtmlAttribute.unapply)

  private val htmlElmementSpecMapping: Mapping[HtmlElementSpec] = mapping(
    idName -> number,
    xpathQueryName -> nonEmptyText,
    awaitedTagName -> nonEmptyText,
    textContentName -> optional(nonEmptyText),
    attributesName -> seq(htmlAttributesMapping)
  )(HtmlElementSpec.apply)(HtmlElementSpec.unapply)

  private val htmlTasksMapping: Mapping[HtmlTask] = mapping(
    textName -> nonEmptyText,
    elementSpecName -> htmlElmementSpecMapping,
  )(HtmlTask.apply)(HtmlTask.unapply)

  // JsTask

  private val jsActionMapping: Mapping[JsAction] = mapping(
    xpathQueryName -> nonEmptyText,
    actionTypeName -> JsActionType.formField,
    keysToSendName -> optional(nonEmptyText),
  )(JsAction.apply)(JsAction.unapply)

  private val jsTasksMapping: Mapping[JsTask] = mapping(
    idName -> number,
    textName -> nonEmptyText,
    preConditionsName -> seq(htmlElmementSpecMapping),
    actionName -> jsActionMapping,
    postConditionsName -> seq(htmlElmementSpecMapping)
  )(JsTask.apply)(JsTask.unapply)

  // WebSampleSolution

  private val webSampleSolutionMapping: Mapping[WebSampleSolution] = mapping(
    idName -> number,
    htmlSampleName -> nonEmptyText,
    jsSampleName -> optional(nonEmptyText)
  )((id, hs, js) => WebSampleSolution(id, WebSolution(hs, js)))(wss => Some((wss.id, wss.sample.htmlSolution, wss.sample.jsSolution)))

  // Site Spec

  private val siteSpecMapping: Mapping[SiteSpec] = mapping(
    idName -> number,
    fileNameName -> nonEmptyText,
    htmlTasksName -> seq(htmlTasksMapping),
    jsTasksName -> seq(jsTasksMapping),
  )(SiteSpec.apply)(SiteSpec.unapply)

  // Complete exercise

  override val collectionFormat: Form[WebCollection] = Form(
    mapping(
      idName -> number,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,
      shortNameName -> nonEmptyText
    )(WebCollection.apply)(WebCollection.unapply)
  )

  override val exerciseFormat: Form[WebExercise] = Form(
    mapping(
      idName -> number,
      semanticVersionName -> SemanticVersionHelper.semanticVersionForm.mapping,
      titleName -> nonEmptyText,
      authorName -> nonEmptyText,
      textName -> nonEmptyText,
      statusName -> ExerciseState.formField,

      htmlTextName -> optional(nonEmptyText),
      jsTextName -> optional(nonEmptyText),

      siteSpecName -> siteSpecMapping,

      samplesName -> seq(webSampleSolutionMapping)
    )(WebExercise.apply)(WebExercise.unapply)
  )

  override val exerciseReviewForm: Form[WebExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )(WebExerciseReview.apply)(WebExerciseReview.unapply)
  )

}
