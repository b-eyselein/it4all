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

  // Correction result writes

  private val umlAttributeMatchWrites: Writes[UmlAttributeMatch] = {
    implicit val umlAttributeWrites              : Writes[UmlAttribute]               = Json.writes
    implicit val umlAttributeAnalysisResultWrites: Writes[UmlAttributeAnalysisResult] = Json.writes

    Json.writes
  }

  private val umlMethodMatchWrites: Writes[UmlMethodMatch] = {
    implicit val umlMethodWrites              : Writes[UmlMethod]               = Json.writes
    implicit val umlMethodAnalysisResultWrites: Writes[UmlMethodAnalysisResult] = Json.writes

    Json.writes
  }

  private val umlClassMatchAnalysisResultWrites: Writes[UmlClassMatchAnalysisResult] = {
    implicit val uamrw: Writes[MatchingResult[UmlAttribute, UmlAttributeAnalysisResult, UmlAttributeMatch]] =
      matchingResultWrites(umlAttributeMatchWrites)

    implicit val ummrw: Writes[MatchingResult[UmlMethod, UmlMethodAnalysisResult, UmlMethodMatch]] =
      matchingResultWrites(umlMethodMatchWrites)

    Json.writes
  }

  private val classMatchWrites: Writes[UmlClassMatch] = {
    implicit val classWrites: Format[UmlClass]                    = UmlClassDiagramJsonFormat.umlClassFormat
    implicit val ucmarw     : Writes[UmlClassMatchAnalysisResult] = umlClassMatchAnalysisResultWrites

    Json.writes
  }

  private val umlAssociationMatchWrites: Writes[UmlAssociationMatch] = {
    implicit val umlAssociationWrites: Writes[UmlAssociation]               = UmlClassDiagramJsonFormat.umlAssocationFormat
    implicit val uamrw               : Writes[UmlAssociationAnalysisResult] = Json.writes

    Json.writes
  }

  private val umlImplementationMatchWrites: Writes[UmlImplementationMatch] = {
    implicit val umlImplementationWrites    : Writes[UmlImplementation]     = UmlClassDiagramJsonFormat.umlImplementationFormat
    implicit val genericAnalysisResultWrites: Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

    Json.writes
  }

  override val completeResultWrites: Writes[UmlCompleteResult] = {
    implicit val pointsWrites: Writes[Points] = ToolJsonProtocol.pointsFormat

    implicit val crw: Writes[MatchingResult[UmlClass, UmlClassMatchAnalysisResult, UmlClassMatch]] =
      matchingResultWrites(classMatchWrites)

    implicit val arw: Writes[MatchingResult[UmlAssociation, UmlAssociationAnalysisResult, UmlAssociationMatch]] =
      matchingResultWrites(umlAssociationMatchWrites)

    implicit val irw: Writes[MatchingResult[UmlImplementation, GenericAnalysisResult, UmlImplementationMatch]] =
      matchingResultWrites(umlImplementationMatchWrites)

    Json.writes[UmlCompleteResult]
  }

}