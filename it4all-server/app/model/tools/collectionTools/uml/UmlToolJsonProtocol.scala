package model.tools.collectionTools.uml

import model.core.matching.{GenericAnalysisResult, MatchingResult}
import model.points._
import model.tools.collectionTools.uml.matcher._
import model.tools.collectionTools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlExerciseContent, UmlClassDiagram, UmlCompleteResult] {

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  override val exerciseContentFormat: Format[UmlExerciseContent] = {
    implicit val ussf: Format[SampleSolution[UmlClassDiagram]] = {
      implicit val ucdf: Format[UmlClassDiagram] = solutionFormat

      Json.format[SampleSolution[UmlClassDiagram]]
    }

    implicit val mf: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format[UmlExerciseContent]
  }

  override val completeResultWrites: Writes[UmlCompleteResult] = {
    implicit val pointsWrites: Writes[Points]                                                                            = ToolJsonProtocol.pointsFormat
    implicit val crw         : Writes[MatchingResult[UmlClass, UmlClassMatchAnalysisResult, UmlClassMatch]]              = _.toJson
    implicit val arw         : Writes[MatchingResult[UmlAssociation, UmlAssociationAnalysisResult, UmlAssociationMatch]] = _.toJson
    implicit val irw         : Writes[MatchingResult[UmlImplementation, GenericAnalysisResult, UmlImplementationMatch]]  = _.toJson

    Json.writes[UmlCompleteResult]
  }

}
