package model.tools.xml

import de.uniwue.dtd.model.ElementLine
import initialData.InitialData
import initialData.xml.XmlInitialData
import model.graphql.ToolGraphQLModelBasics
import model.matching.MatchingResult
import model.tools._
import model.{Exercise, Topic, User}

import scala.concurrent.{ExecutionContext, Future}

object XmlTool extends Tool("xml", "Xml") {

  override type SolutionInputType = XmlSolution
  override type ExContentType     = XmlExerciseContent
  override type PartType          = XmlExPart
  override type ResType           = XmlResult

  type XmlExercise = Exercise[XmlExerciseContent]

  type ElementLineComparison = MatchingResult[ElementLine, ElementLineMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: ToolJsonProtocol[XmlSolution, XmlExerciseContent, XmlExPart] = XmlToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[XmlSolution, XmlExerciseContent, XmlExPart, XmlResult] = XmlGraphQLModels

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

  override val initialData: InitialData[XmlExerciseContent] = XmlInitialData.initialData

  override val allTopics: Seq[Topic] = Seq.empty

}
