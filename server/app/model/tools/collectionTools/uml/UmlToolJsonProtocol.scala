package model.tools.collectionTools.uml

import model.core.matching.MatchingResult
import model.points._
import model.tools.collectionTools.uml.matcher.{UmlAssociationMatch, UmlClassMatch, UmlImplementationMatch}
import model.tools.collectionTools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[ UmlExerciseContent, UmlClassDiagram, UmlCompleteResult] {

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  override val exerciseContentFormat: Format[UmlExerciseContent] = {
    implicit val ussf: Format[SampleSolution[UmlClassDiagram]] = {
      implicit val ucdf: Format[UmlClassDiagram] = solutionFormat

      Json.format[SampleSolution[UmlClassDiagram]]
    }

    Json.format[UmlExerciseContent]
  }

  override val completeResultWrites: Writes[UmlCompleteResult] = {
    implicit val pointsWrites: Writes[Points]                                 = ToolJsonProtocol.pointsFormat
    implicit val crw         : Writes[MatchingResult[UmlClassMatch]]          = _.toJson
    implicit val arw         : Writes[MatchingResult[UmlAssociationMatch]]    = _.toJson
    implicit val irw         : Writes[MatchingResult[UmlImplementationMatch]] = _.toJson

    Json.writes[UmlCompleteResult]
  }

}
