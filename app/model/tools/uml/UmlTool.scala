package model.tools.uml

import model.User
import model.core.matching.MatchingResult
import model.tools._
import model.tools.uml.matcher._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object UmlTool extends CollectionTool("uml", "Uml", ToolState.BETA) {

  override type SolType        = UmlClassDiagram
  override type ExContentType  = UmlExerciseContent
  override type PartType       = UmlExPart
  override type CompResultType = UmlCompleteResult

  type ClassComparison          = MatchingResult[UmlClass, UmlClassMatch]
  type AttributeComparison      = MatchingResult[UmlAttribute, UmlAttributeMatch]
  type MethodComparison         = MatchingResult[UmlMethod, UmlMethodMatch]
  type AssociationComparison    = MatchingResult[UmlAssociation, UmlAssociationMatch]
  type ImplementationComparison = MatchingResult[UmlImplementation, UmlImplementationMatch]

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] =
    UmlToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[UmlClassDiagram, UmlExerciseContent, UmlExPart] =
    UmlGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    classDiagram: UmlClassDiagram,
    collection: ExerciseCollection,
    exercise: Exercise[UmlClassDiagram, UmlExerciseContent],
    part: UmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[UmlCompleteResult]] = Future.successful {
    // FIXME: compare against every sample solution, take best?
    exercise.content.sampleSolutions.headOption match {
      case None => Failure(new Exception("There is no sample solution!"))
      case Some(sampleSolution) =>
        Success(UmlCorrector.correct(classDiagram, sampleSolution.sample, part, solutionSaved))
    }
  }

}
