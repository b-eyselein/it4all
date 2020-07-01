package model.tools.uml

import initialData.InitialData
import initialData.uml.UmlInitialData
import model.core.matching.MatchingResult
import model.graphql.ToolGraphQLModelBasics
import model.tools._
import model.tools.uml.matcher._
import model.{Exercise, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}

object UmlTool extends Tool("uml", "Uml", ToolState.BETA) {

  override type SolType       = UmlClassDiagram
  override type ExContentType = UmlExerciseContent
  override type PartType      = UmlExPart
  override type ResType       = UmlAbstractResult

  type UmlExercise = Exercise[UmlExerciseContent]

  type ClassComparison          = MatchingResult[UmlClass, UmlClassMatch]
  type AttributeComparison      = MatchingResult[UmlAttribute, UmlAttributeMatch]
  type MethodComparison         = MatchingResult[UmlMethod, UmlMethodMatch]
  type AssociationComparison    = MatchingResult[UmlAssociation, UmlAssociationMatch]
  type ImplementationComparison = MatchingResult[UmlImplementation, UmlImplementationMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] = UmlToolJsonProtocol

  override val graphQlModels
    : ToolGraphQLModelBasics[UmlClassDiagram, UmlExerciseContent, UmlExPart, UmlAbstractResult] =
    UmlGraphQLModels

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: UmlClassDiagram,
    exercise: UmlExercise,
    part: UmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[UmlAbstractResult] =
    Future.successful {
      UmlCorrector.correct(solution, exercise, part, solutionSaved)
    }

  override val initialData: InitialData[UmlExerciseContent] = UmlInitialData

}
