package model.tools.uml

import model.core.matching.MatchingResult
import model.core.result.{CompleteResultJsonProtocol, EvaluationResult}
import model.tools.uml.matcher.{UmlAssociationMatch, UmlClassMatch, UmlImplementationMatch}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object UmlSampleSolutionJsonProtocol {

  private implicit val umlClassDiagramJsonFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlSolutionJsonFormat

  val umlSampleSolutionFormat: Format[UmlSampleSolution] = Json.format[UmlSampleSolution]

}

object UmlCompleteResultJsonProtocol extends CompleteResultJsonProtocol[EvaluationResult, UmlCompleteResult] {

  override def completeResultWrites(solutionSaved: Boolean): Writes[UmlCompleteResult] = (
    (__ \ "classResult").write[Option[MatchingResult[UmlClassMatch]]] and
      (__ \ "assocAndImplResult").write[Option[(MatchingResult[UmlAssociationMatch], MatchingResult[UmlImplementationMatch])]]
    ) (ucr => (ucr.classResult, ucr.assocAndImplResult)
  )

  private implicit def classResultWrites: Writes[MatchingResult[UmlClassMatch]] = _.toJson

  private implicit def assocAndImplResultWrites: Writes[(MatchingResult[UmlAssociationMatch], MatchingResult[UmlImplementationMatch])] = (
    (__ \ "assocResult").write[MatchingResult[UmlAssociationMatch]] and
      (__ \ "implResult").write[MatchingResult[UmlImplementationMatch]]
    ) (x => (x._1, x._2))

  private implicit def assocResultWrites: Writes[MatchingResult[UmlAssociationMatch]] = _.toJson

  private implicit def implResultWrites: Writes[MatchingResult[UmlImplementationMatch]] = _.toJson

}
