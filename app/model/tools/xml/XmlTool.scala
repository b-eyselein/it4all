package model.tools.xml

import de.uniwue.dtd.model.ElementLine
import initialData.xml.XmlInitialData
import model.matching.MatchingResult
import model.tools._
import model.{Exercise, User}

import scala.concurrent.{ExecutionContext, Future}

object XmlTool extends ToolWithParts("xml", "Xml") {

  override type SolInputType = XmlSolution
  override type ExContType   = XmlExerciseContent
  override type PartType     = XmlExPart
  override type ResType      = XmlResult

  type XmlExercise = Exercise[XmlExerciseContent]

  type ElementLineComparison = MatchingResult[ElementLine, ElementLineMatch]

  override val jsonFormats   = XmlToolJsonProtocol
  override val graphQlModels = XmlGraphQLModels
  override val initialData   = XmlInitialData.initialData

  // Correction

  override def correctAbstract(
    user: User,
    solution: XmlSolution,
    exercise: XmlExercise,
    part: XmlExPart
  )(implicit executionContext: ExecutionContext): Future[XmlResult] = Future.fromTry {
    part match {
      case XmlExPart.GrammarCreationXmlPart => XmlCorrector.correctGrammar(solution, exercise.content.sampleSolutions)

      case XmlExPart.DocumentCreationXmlPart =>
        XmlCorrector.correctDocument(
          solution,
          solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId).createDirectories(),
          exercise.content
        )
    }
  }

}
