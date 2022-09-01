package model.tools.ebnf

import initialData.InitialData
import initialData.ebnf.EbnfInitialData
import model.graphql.ToolWithoutPartsGraphQLModel
import model.tools.{ToolWithoutParts, ToolWithoutPartsJsonProtocol}
import model.{Exercise, User}

import scala.concurrent.{ExecutionContext, Future}

object EbnfTool extends ToolWithoutParts("ebnf", "EBNF") {

  override type SolInputType = EbnfGrammar
  override type ExContType     = EbnfExerciseContent
  override type ResType           = EbnfResult

  type EbnfExercise = Exercise[EbnfExerciseContent]

  override val jsonFormats: ToolWithoutPartsJsonProtocol[EbnfGrammar, EbnfExerciseContent] = EbnfToolJsonProtocol

  override val graphQlModels: ToolWithoutPartsGraphQLModel[EbnfGrammar, EbnfExerciseContent, EbnfResult] = EbnfGraphQLModels

  override def correctAbstract(
    user: User,
    solution: EbnfGrammar,
    exercise: EbnfExercise
  )(implicit executionContext: ExecutionContext): Future[EbnfResult] = ???

  override val initialData: InitialData[EbnfExerciseContent] = EbnfInitialData.initialData

}
