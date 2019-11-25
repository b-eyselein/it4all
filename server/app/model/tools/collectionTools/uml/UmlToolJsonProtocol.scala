package model.tools.collectionTools.uml

import model.core.matching.MatchingResult
import model.points._
import model.tools.collectionTools.ToolJsonProtocol
import model.tools.collectionTools.uml.matcher.{UmlAssociationMatch, UmlClassMatch, UmlImplementationMatch}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlExPart, UmlExerciseContent, UmlClassDiagram, UmlUserSolution, UmlCompleteResult] {

  override val userSolutionFormat: Format[UmlUserSolution] = {
    implicit val uepf: Format[UmlExPart]       = UmlExParts.jsonFormat
    implicit val ucdf: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat
    implicit val pf  : Format[Points]          = ToolJsonProtocol.pointsFormat

    Json.format[UmlUserSolution]
  }

  override val exerciseContentFormat: Format[UmlExerciseContent] = {
    implicit val ussf: Format[UmlSampleSolution] = {
      implicit val ucdf: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

      Json.format[UmlSampleSolution]
    }

    Json.format[UmlExerciseContent]
  }

  override val completeResultWrites: Writes[UmlCompleteResult] = {
    implicit val pointsWrites: Writes[Points]                                 = pointsJsonWrites
    implicit val crw         : Writes[MatchingResult[UmlClassMatch]]          = _.toJson
    implicit val arw         : Writes[MatchingResult[UmlAssociationMatch]]    = _.toJson
    implicit val irw         : Writes[MatchingResult[UmlImplementationMatch]] = _.toJson

    Json.writes[UmlCompleteResult]
  }

}
