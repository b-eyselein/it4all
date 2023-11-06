package model.tools.xml

import de.uniwue.dtd.model.ElementLine
import initialData.InitialData
import initialData.xml.XmlInitialData
import model.graphql.ToolWithPartsGraphQLModel
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

  // Yaml, Html forms, Json

  override val jsonFormats: ToolWithPartsJsonProtocol[XmlSolution, XmlExerciseContent, XmlExPart] = XmlToolJsonProtocol

  override val graphQlModels: ToolWithPartsGraphQLModel[XmlSolution, XmlExerciseContent, XmlResult, XmlExPart] = XmlGraphQLModels

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

}
