package model.core

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import model.core.EventType.EventType
import play.api.mvc.{AnyContent, Request}

object WorkingEvent {
  val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss Z")
}

object EventType extends Enumeration {
  type EventType = Value
  val EXERCISE_START, EXERCISE_CORRECTION, EXERCISE_COMPLETION = Value
}

abstract class WorkingEvent(eventType: EventType, request: Request[AnyContent], id: Int) {

  val dateTime: ZonedDateTime = ZonedDateTime.now
  val uri     : String        = request.uri
  val method  : String        = request.method

  def getDateTime: String = dateTime.format(WorkingEvent.formatter)

}

case class ExerciseStartEvent(request: Request[AnyContent], id: Int) extends WorkingEvent(EventType.EXERCISE_START, request, id)

//abstract class ResultEvent(e: EventType, r: Request[AnyContent], i: Int, @JsonIgnore val results: CompleteResult[_ <: EvaluationResult]) extends WorkingEvent(e, r, i)

class ExerciseCorrectionEvent[E <: EvaluationResult](r: Request[AnyContent], i: Int, rs: CompleteResult[E]) extends WorkingEvent(EventType.EXERCISE_CORRECTION, r, i)

class ExerciseCompletionEvent[E <: EvaluationResult](r: Request[AnyContent], i: Int, rs: CompleteResult[E]) extends WorkingEvent(EventType.EXERCISE_COMPLETION, r, i)
