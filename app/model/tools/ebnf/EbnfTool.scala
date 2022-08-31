package model.tools.ebnf

import initialData.InitialData
import initialData.ebnf.EbnfInitialData
import model.graphql.ToolGraphQLModelBasics
import model.tools.{Tool, ToolJsonProtocol}
import model.{Exercise, Topic, User}

import scala.concurrent.{ExecutionContext, Future}

object EbnfTool extends Tool("ebnf", "EBNF") {

  override type SolutionInputType = EbnfGrammar
  override type ExContentType     = EbnfExerciseContent
  override type PartType          = EbnfExercisePart
  override type ResType           = EbnfResult

  type EbnfExercise = Exercise[EbnfExerciseContent]

  override val jsonFormats: ToolJsonProtocol[EbnfGrammar, EbnfExerciseContent, EbnfExercisePart] = EbnfToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[EbnfGrammar, EbnfExerciseContent, EbnfExercisePart, EbnfResult] = EbnfGraphQLModels

  override def correctAbstract(
    user: User,
    solution: EbnfGrammar,
    exercise: EbnfExercise,
    part: EbnfExercisePart
  )(implicit executionContext: ExecutionContext): Future[EbnfResult] = ???

  override val initialData: InitialData[EbnfExerciseContent] = EbnfInitialData.initialData

  override val allTopics: Seq[Topic] = Seq.empty

}
