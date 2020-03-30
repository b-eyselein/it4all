package model.tools.collectionTools.xml

import de.uniwue.dtd.model.ElementLine
import model.User
import model.core.matching.MatchingResult
import model.tools.collectionTools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object XmlToolMain extends CollectionToolMain(XmlConsts) {

  override type PartType       = XmlExPart
  override type ExContentType  = XmlExerciseContent
  override type SolType        = XmlSolution
  override type CompResultType = XmlCompleteResult

  type ElementLineComparison = MatchingResult[ElementLine, ElementLineMatch]

  // Other members

  override val hasPlayground           = true
  override val exParts: Seq[XmlExPart] = XmlExParts.values

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[XmlExerciseContent, XmlSolution, XmlCompleteResult, XmlExPart] =
    XmlToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[XmlExerciseContent, XmlSolution, XmlCompleteResult, XmlExPart] =
    XmlGraphQLModels

  // Correction

  override protected def correctEx(
    user: User,
    solution: XmlSolution,
    collection: ExerciseCollection,
    exercise: Exercise,
    content: XmlExerciseContent,
    part: XmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[XmlCompleteResult]] = Future.successful(
    part match {
      case XmlExParts.GrammarCreationXmlPart => XmlCorrector.correctGrammar(solution, content, solutionSaved)
      case XmlExParts.DocumentCreationXmlPart =>
        XmlCorrector.correctDocument(
          solution,
          solutionDirForExercise(user.username, collection.id, exercise.id).createDirectories(),
          content,
          solutionSaved
        )
    }
  )

}
