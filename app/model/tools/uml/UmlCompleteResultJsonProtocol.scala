package model.tools.uml

import model.core.matching.MatchingResult
import model.core.result.{CompleteResultJsonProtocol, EvaluationResult}
import model.points._
import model.tools.uml.matcher.{UmlAssociationMatch, UmlClassMatch, UmlImplementationMatch}
import play.api.libs.json._

object UmlCompleteResultJsonProtocol extends CompleteResultJsonProtocol[EvaluationResult, UmlCompleteResult] {


  val umlSampleSolutionFormat: Format[UmlSampleSolution] = {
    implicit val ucdf: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

    Json.format[UmlSampleSolution]
  }


  private val classResultWrites: Writes[MatchingResult[UmlClassMatch]] = _.toJson

  private val assocResultWrites: Writes[MatchingResult[UmlAssociationMatch]] = _.toJson

  private val implResultWrites: Writes[MatchingResult[UmlImplementationMatch]] = _.toJson


  override val completeResultWrites: Writes[UmlCompleteResult] = {
    implicit val pointsWrites: Writes[Points] = pointsJsonWrites

    implicit val crw: Writes[MatchingResult[UmlClassMatch]] = classResultWrites

    implicit val arw: Writes[MatchingResult[UmlAssociationMatch]] = assocResultWrites

    implicit val irw: Writes[MatchingResult[UmlImplementationMatch]] = implResultWrites

    Json.writes[UmlCompleteResult]
  }

}
