package model.tools.uml

import model.core.matching.MatchingResult
import model.points._
import model.tools.ToolJsonProtocol
import model.tools.uml.matcher.{UmlAssociationMatch, UmlClassMatch, UmlImplementationMatch}
import model.{LongText, LongTextJsonProtocol, SemanticVersion, SemanticVersionHelper}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlExercise, UmlSampleSolution, UmlCompleteResult] {

  // Other

  override val sampleSolutionFormat: Format[UmlSampleSolution] = {
    implicit val ucdf: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

    Json.format[UmlSampleSolution]
  }

  override val exerciseFormat: Format[UmlExercise] = {
    implicit val svf : Format[SemanticVersion]   = SemanticVersionHelper.format
    implicit val ltf : Format[LongText]          = LongTextJsonProtocol.format
    implicit val ussf: Format[UmlSampleSolution] = sampleSolutionFormat

    Json.format[UmlExercise]
  }

  override val completeResultWrites: Writes[UmlCompleteResult] = {
    implicit val pointsWrites: Writes[Points]                                 = pointsJsonWrites
    implicit val crw         : Writes[MatchingResult[UmlClassMatch]]          = _.toJson
    implicit val arw         : Writes[MatchingResult[UmlAssociationMatch]]    = _.toJson
    implicit val irw         : Writes[MatchingResult[UmlImplementationMatch]] = _.toJson

    Json.writes[UmlCompleteResult]
  }

}
