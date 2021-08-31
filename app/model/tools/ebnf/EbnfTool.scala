package model.tools.ebnf

import initialData.InitialData
import initialData.ebnf.EbnfInitialData
import model.graphql.ToolGraphQLModelBasics
import model.tools.ebnf.parsing.ExtendedEbnfParser
import model.tools.{Tool, ToolJsonProtocol}
import model.{Exercise, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object EbnfTool extends Tool("ebnf", "EBNF") {

  override type SolutionInputType = EbnfGrammar
  override type ExContentType     = EbnfExerciseContent
  override type PartType          = EbnfExercisePart
  override type ResType           = EbnfResult

  type EbnfExercise = Exercise[EbnfExerciseContent]

  override val jsonFormats: ToolJsonProtocol[EbnfGrammar, EbnfExerciseContent, EbnfExercisePart] = EbnfToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[EbnfGrammar, EbnfExerciseContent, EbnfExercisePart, EbnfResult] = EbnfGraphQLModels

  override def correctAbstract(
    user: LoggedInUser,
    solution: EbnfGrammar,
    exercise: EbnfExercise,
    part: EbnfExercisePart
  )(implicit executionContext: ExecutionContext): Future[Try[EbnfResult]] = Future.successful {

    println(solution)

    val startSymbol = solution.startSymbol
    val rules       = solution.rules

    val x = ExtendedEbnfParser.grammar

    ???
  }

  override val initialData: InitialData[EbnfExerciseContent] = EbnfInitialData

}
