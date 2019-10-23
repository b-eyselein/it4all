package model.tools.nary

import javax.inject.{Inject, Singleton}
import model.core.result.EvaluationResult
import model.toolMains.{RandomExerciseToolMain, ToolState}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

@Singleton
class NaryToolMain @Inject()(val tables: NaryTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain("Zahlensysteme", "nary") {

  // Abstract types

  override type PartType = NaryExPart

  override type ResultType = EvaluationResult

  override type Tables = NaryTableDefs

  // Other members

  override val toolState: ToolState = ToolState.LIVE


  // Views

  override def exercisesOverviewForIndex: Html = Html("")

}
