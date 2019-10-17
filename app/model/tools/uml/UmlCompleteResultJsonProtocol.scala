package model.tools.uml

import model.{SemanticVersion, SemanticVersionHelper}
import model.core.matching.MatchingResult
import model.core.result.{CompleteResultJsonProtocol, EvaluationResult}
import model.points._
import model.tools.uml.matcher.{UmlAssociationMatch, UmlClassMatch, UmlImplementationMatch}
import play.api.libs.json._

object UmlCompleteResultJsonProtocol extends CompleteResultJsonProtocol[EvaluationResult, UmlCompleteResult] {

  val exerciseFormat: Format[UmlExercise] = {
    implicit val svf: Format[SemanticVersion] = SemanticVersionHelper.format

    implicit val ussf: Format[UmlSampleSolution] = umlSampleSolutionFormat

    Json.format[UmlExercise]
  }

  // Other

  val umlSampleSolutionFormat: Format[UmlSampleSolution] = {
    implicit val ucdf: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

    Json.format[UmlSampleSolution]
  }

  override val completeResultWrites: Writes[UmlCompleteResult] = {
    implicit val pointsWrites: Writes[Points] = pointsJsonWrites

    implicit val crw: Writes[MatchingResult[UmlClassMatch]] = _.toJson

    implicit val arw: Writes[MatchingResult[UmlAssociationMatch]] = _.toJson

    implicit val irw: Writes[MatchingResult[UmlImplementationMatch]] = _.toJson

    Json.writes[UmlCompleteResult]
  }

}
