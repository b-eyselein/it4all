package model.tools.uml

import initialData.uml.UmlInitialData
import model.graphql.ToolWithPartsGraphQLModel
import model.matching.MatchingResult
import model.tools.ToolWithParts
import model.tools.uml.matcher.{UmlAssociationMatch, UmlAttributeMatch, UmlClassMatch, UmlImplementationMatch, UmlMethodMatch}
import model.{Exercise, User}

import scala.concurrent.{ExecutionContext, Future}

object UmlTool extends ToolWithParts("uml", "Uml", true) {

  override type SolInputType = UmlClassDiagram
  override type ExContType   = UmlExerciseContent
  override type PartType     = UmlExPart
  override type ResType      = UmlResult

  type UmlExercise = Exercise[UmlExerciseContent]

  type ClassComparison          = MatchingResult[UmlClass, UmlClassMatch]
  type AttributeComparison      = MatchingResult[UmlAttribute, UmlAttributeMatch]
  type MethodComparison         = MatchingResult[UmlMethod, UmlMethodMatch]
  type AssociationComparison    = MatchingResult[UmlAssociation, UmlAssociationMatch]
  type ImplementationComparison = MatchingResult[UmlImplementation, UmlImplementationMatch]

  override val jsonFormats                                                                                         = UmlToolJsonProtocol
  override val graphQlModels: ToolWithPartsGraphQLModel[UmlClassDiagram, UmlExerciseContent, UmlResult, UmlExPart] = UmlGraphQLModels
  override val initialData                                                                                         = UmlInitialData.initialData

  // Correction

  override def correctAbstract(
    user: User,
    solution: UmlClassDiagram,
    exercise: UmlExercise,
    part: UmlExPart
  )(implicit executionContext: ExecutionContext): Future[UmlResult] = Future.fromTry { UmlCorrector.correct(solution, exercise, part) }

}
