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
    collection: ExerciseCollection,
    exercise: Exercise,
    exerciseContent: XmlExerciseContent,
    sampleSolutions: Seq[SampleSolution[XmlSolution]],
    part: XmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[XmlCompleteResult]] = Future.successful(
    part match {
      case XmlExParts.GrammarCreationXmlPart =>
        XmlCorrector.correctGrammar(solution, sampleSolutions, solutionSaved)

      case XmlExParts.DocumentCreationXmlPart =>
        XmlCorrector.correctDocument(
          solution,
          solutionDirForExercise(user.username, collection.id, exercise.id).createDirectories(),
          exerciseContent,
          sampleSolutions,
          solutionSaved
        )
    }
  )

}
