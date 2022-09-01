package model.tools.uml

import initialData.InitialData
import initialData.uml.UmlInitialData
import model.graphql.ToolWithPartsGraphQLModel
import model.matching.MatchingResult
import model.tools._
import model.tools.uml.matcher._
import model.{Exercise, User}

import scala.concurrent.{ExecutionContext, Future}

object UmlTool extends ToolWithParts("uml", "Uml", true) {

  override type SolInputType = UmlClassDiagram
  override type ExContType     = UmlExerciseContent
  override type PartType          = UmlExPart
  override type ResType           = UmlResult

  type UmlExercise = Exercise[UmlExerciseContent]

  type ClassComparison          = MatchingResult[UmlClass, UmlClassMatch]
  type AttributeComparison      = MatchingResult[UmlAttribute, UmlAttributeMatch]
  type MethodComparison         = MatchingResult[UmlMethod, UmlMethodMatch]
  type AssociationComparison    = MatchingResult[UmlAssociation, UmlAssociationMatch]
  type ImplementationComparison = MatchingResult[UmlImplementation, UmlImplementationMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: ToolWithPartsJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] = UmlToolJsonProtocol

  override val graphQlModels: ToolWithPartsGraphQLModel[UmlClassDiagram, UmlExerciseContent, UmlResult, UmlExPart] = UmlGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    solution: UmlClassDiagram,
    exercise: UmlExercise,
    part: UmlExPart
  )(implicit executionContext: ExecutionContext): Future[UmlResult] = Future.fromTry { UmlCorrector.correct(solution, exercise, part) }

  override val initialData: InitialData[UmlExerciseContent] = UmlInitialData.initialData

}
