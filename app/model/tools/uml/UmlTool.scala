package model.tools.uml

import initialData.InitialData
import initialData.uml.UmlInitialData
import model.graphql.ToolGraphQLModelBasics
import model.matching.MatchingResult
import model.tools._
import model.tools.uml.matcher._
import model.{Exercise, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object UmlTool extends Tool("uml", "Uml", true) {

  override type SolutionInputType = UmlClassDiagram
  override type ExContentType     = UmlExerciseContent
  override type PartType          = UmlExPart
  override type ResType           = UmlResult

  type UmlExercise = Exercise[UmlExerciseContent]

  type ClassComparison          = MatchingResult[UmlClass, UmlClassMatch]
  type AttributeComparison      = MatchingResult[UmlAttribute, UmlAttributeMatch]
  type MethodComparison         = MatchingResult[UmlMethod, UmlMethodMatch]
  type AssociationComparison    = MatchingResult[UmlAssociation, UmlAssociationMatch]
  type ImplementationComparison = MatchingResult[UmlImplementation, UmlImplementationMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] = UmlToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[UmlClassDiagram, UmlExerciseContent, UmlExPart, UmlResult] = UmlGraphQLModels

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: UmlClassDiagram,
    exercise: UmlExercise,
    part: UmlExPart
  )(implicit executionContext: ExecutionContext): Future[Try[UmlResult]] = Future.successful { UmlCorrector.correct(solution, exercise, part) }

  override val initialData: InitialData[UmlExerciseContent] = UmlInitialData

}
