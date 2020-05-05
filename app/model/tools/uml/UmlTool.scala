package model.tools.uml

import model.{Exercise, LoggedInUser}
import model.core.matching.MatchingResult
import model.graphql.ToolGraphQLModelBasics
import model.tools._
import model.tools.uml.matcher._

import scala.concurrent.{ExecutionContext, Future}

object UmlTool extends CollectionTool("uml", "Uml", ToolState.BETA) {

  override type SolType       = UmlClassDiagram
  override type ExContentType = UmlExerciseContent
  override type PartType      = UmlExPart
  override type ResType       = UmlAbstractResult

  type ClassComparison          = MatchingResult[UmlClass, UmlClassMatch]
  type AttributeComparison      = MatchingResult[UmlAttribute, UmlAttributeMatch]
  type MethodComparison         = MatchingResult[UmlMethod, UmlMethodMatch]
  type AssociationComparison    = MatchingResult[UmlAssociation, UmlAssociationMatch]
  type ImplementationComparison = MatchingResult[UmlImplementation, UmlImplementationMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] =
    UmlToolJsonProtocol

  override val graphQlModels
    : ToolGraphQLModelBasics[UmlClassDiagram, UmlExerciseContent, UmlExPart, UmlAbstractResult] =
    UmlGraphQLModels

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: UmlClassDiagram,
    exercise: Exercise[UmlClassDiagram, UmlExerciseContent],
    part: UmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[UmlAbstractResult] = Future.successful {
    UmlCorrector.correct(solution, exercise, part, solutionSaved)
  }

}
