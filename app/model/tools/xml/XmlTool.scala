package model.tools.xml

import de.uniwue.dtd.model.ElementLine
import model.User
import model.core.matching.MatchingResult
import model.persistence.DbExercise
import model.tools._
import play.api.libs.json.Reads

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object XmlTool extends CollectionTool("xml", "Xml") {

  override type SolType        = XmlSolution
  override type ExContentType  = XmlExerciseContent
  override type ExerciseType   = XmlExercise
  override type PartType       = XmlExPart
  override type CompResultType = XmlCompleteResult

  type ElementLineComparison = MatchingResult[ElementLine, ElementLineMatch]

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[XmlSolution, XmlExerciseContent, XmlExercise, XmlExPart] =
    XmlToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[XmlSolution, XmlExerciseContent, XmlExercise, XmlExPart] =
    XmlGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    solution: XmlSolution,
    collection: ExerciseCollection,
    exercise: XmlExercise,
    part: XmlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[XmlCompleteResult]] = Future.successful(
    part match {
      case XmlExParts.GrammarCreationXmlPart =>
        XmlCorrector.correctGrammar(solution, exercise, solutionSaved)

      case XmlExParts.DocumentCreationXmlPart =>
        XmlCorrector.correctDocument(
          solution,
          solutionDirForExercise(user.username, collection.id, exercise.id).createDirectories(),
          exercise,
          solutionSaved
        )
    }
  )

  override protected def convertExerciseFromDb(dbExercise: DbExercise, topics: Seq[Topic]): Option[XmlExercise] =
    dbExercise match {
      case DbExercise(id, collectionId, toolId, title, authors, text, difficulty, sampleSolutionsJson, contentJson) =>
        for {
          sampleSolutions <- Reads.seq(toolJsonProtocol.sampleSolutionFormat).reads(sampleSolutionsJson).asOpt
          content         <- toolJsonProtocol.exerciseContentFormat.reads(contentJson).asOpt
        } yield XmlExercise(
          id,
          collectionId,
          toolId,
          title,
          authors,
          text,
          topics,
          difficulty,
          sampleSolutions,
          content
        )
    }

}
