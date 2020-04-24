package model.tools.xml

import de.uniwue.dtd.model.ElementLine
import model.User
import model.core.matching.MatchingResult
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object XmlTool extends CollectionTool("xml", "Xml") {

  override type SolType        = XmlSolution
  override type ExContentType  = XmlExerciseContent
  override type PartType       = XmlExPart
  override type CompResultType = XmlCompleteResult

  type ElementLineComparison = MatchingResult[ElementLine, ElementLineMatch]

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[XmlSolution, XmlExerciseContent, XmlExPart] =
    XmlToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[XmlSolution, XmlExerciseContent, XmlExPart] =
    XmlGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    solution: XmlSolution,
    exercise: Exercise[XmlSolution, XmlExerciseContent],
    part: XmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[XmlCompleteResult]] = Future.successful(
    part match {
      case XmlExPart.GrammarCreationXmlPart =>
        XmlCorrector.correctGrammar(solution, exercise.content.sampleSolutions, solutionSaved)

      case XmlExPart.DocumentCreationXmlPart =>
        XmlCorrector.correctDocument(
          solution,
          solutionDirForExercise(user.username, exercise.collectionId, exercise.id).createDirectories(),
          exercise.content,
          solutionSaved
        )
    }
  )

}
