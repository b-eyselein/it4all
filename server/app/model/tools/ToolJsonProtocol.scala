package model.tools

import model.core.result.{CompleteResult, EvaluationResult}
import model._
import play.api.libs.json.{Format, Json, Writes}


trait ToolJsonProtocol[E <: Exercise, SS <: SampleSolution[_], CR <: CompleteResult[_ <: EvaluationResult]] {

  val exerciseFormat: Format[E]

  val completeResultWrites: Writes[CR]

  val sampleSolutionFormat: Format[SS]


}


trait FilesSampleSolutionToolJsonProtocol[E <: Exercise, CR <: CompleteResult[_ <: EvaluationResult]]
  extends ToolJsonProtocol[E, FilesSampleSolution, CR] {

  override final val sampleSolutionFormat: Format[FilesSampleSolution] = {
    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    Json.format[FilesSampleSolution]
  }

}
