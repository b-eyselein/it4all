package model.tools.collectionTools.uml

import model.User
import model.core.matching.MatchingResult
import model.tools.collectionTools.uml.matcher._
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, ToolJsonProtocol}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object UmlToolMain extends CollectionToolMain(UmlConsts) {

  override type PartType = UmlExPart
  override type ExContentType = UmlExerciseContent
  override type SolType = UmlClassDiagram
  override type CompResultType = UmlCompleteResult

  type ClassComparison = MatchingResult[UmlClass, UmlClassMatch]
  type AttributeComparison = MatchingResult[UmlAttribute, UmlAttributeMatch]
  type MethodComparison = MatchingResult[UmlMethod, UmlMethodMatch]
  type AssociationComparison = MatchingResult[UmlAssociation, UmlAssociationMatch]
  type ImplementationComparison = MatchingResult[UmlImplementation, UmlImplementationMatch]

  // Other members

  override val exParts: Seq[UmlExPart] = UmlExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[UmlExerciseContent, UmlClassDiagram, UmlCompleteResult] =
    UmlToolJsonProtocol

  // Correction

  override def correctEx(
    user: User,
    classDiagram: UmlClassDiagram,
    collection: ExerciseCollection,
    exercise: Exercise,
    content: UmlExerciseContent,
    part: UmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[UmlCompleteResult]] = Future.successful {
    // FIXME: compare against every sample solution, take best?
    content.sampleSolutions.headOption match {
      case None                 => Failure(new Exception("There is no sample solution!"))
      case Some(sampleSolution) => Success(UmlCorrector.correct(classDiagram, sampleSolution.sample, part, solutionSaved))
    }
  }

}
