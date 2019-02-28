package model.tools.web

import model.core.ExerciseForm
import model.tools.web.WebConsts._
import model.{Difficulties, ExerciseState, SemanticVersionHelper}
import play.api.data.Forms._
import play.api.data.{Form, Mapping}

object WebExerciseForm extends ExerciseForm[WebExercise, WebCollection, WebExerciseReview] {

  // HtmlTask

  private val htmlAttributesMapping: Mapping[HtmlAttribute] = mapping(
    keyName -> nonEmptyText,
    valueName -> nonEmptyText
  )(HtmlAttribute.apply)(HtmlAttribute.unapply)

  private val htmlTasksMapping: Mapping[HtmlTask] = mapping(
    idName -> number,
    textName -> nonEmptyText,
    xpathQueryName -> nonEmptyText,
    textContentName -> optional(nonEmptyText),
    attributesName -> seq(htmlAttributesMapping)
  )(HtmlTask.apply)(HtmlTask.unapply)

  // JsTask

  private val jsConditionMapping: Mapping[JsCondition] = mapping(
    idName -> number,
    xpathQueryName -> nonEmptyText,
    IS_PRECOND_NAME -> boolean,
    awaitedName -> nonEmptyText
  )(JsCondition.apply)(JsCondition.unapply)

  private val jsTasksMapping: Mapping[JsTask] = mapping(
    idName -> number,
    textName -> nonEmptyText,
    xpathQueryName -> nonEmptyText,
    actionTypeName -> JsActionType.formField,
    keysToSendName -> optional(nonEmptyText),
    conditionsName -> seq(jsConditionMapping)
  )(JsTask.apply)(JsTask.unapply)

  // WebSampleSolution

  private val webSampleSolutionMapping: Mapping[WebSampleSolution] = mapping(
    idName -> number,
    htmlSampleName -> nonEmptyText,
    jsSampleName -> nonEmptyText
  )((id, hs, js) => WebSampleSolution(id, WebSolution(hs, js)))(wss => Some((wss.id, wss.sample.htmlSolution, wss.sample.jsSolution)))

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

      htmlTasksName -> seq(htmlTasksMapping),
      jsTasksName -> seq(jsTasksMapping),

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
