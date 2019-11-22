package model.tools.collectionTools.xml

import model._
import model.points.Points
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, ToolJsonProtocol}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try


object XmlToolMain extends CollectionToolMain(XmlConsts) {

  // Result types

  override type PartType = XmlExPart
  override type ExContentType = XmlExerciseContent

  override type SolType = XmlSolution
  override type SampleSolType = XmlSampleSolution
  override type UserSolType = XmlUserSolution

  override type ResultType = XmlEvaluationResult
  override type CompResultType = XmlCompleteResult


  // Other members

  override val hasPlayground           = true
  override val exParts: Seq[XmlExPart] = XmlExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[XmlExPart, XmlExerciseContent, XmlSolution, XmlSampleSolution, XmlUserSolution, XmlCompleteResult] =
    XmlToolJsonProtocol

  override protected val exerciseContentYamlFormat: YamlFormat[XmlExerciseContent] = XmlExYamlProtocol.xmlExerciseYamlFormat

  // Other helper methods

  override def instantiateSolution(id: Int, exercise: Exercise, part: XmlExPart, solution: XmlSolution, points: Points, maxPoints: Points): XmlUserSolution =
    XmlUserSolution(id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: XmlCompleteResult, solSaved: Boolean): XmlCompleteResult = compResult match {
    case dr: XmlDocumentCompleteResult => dr.copy(solutionSaved = solSaved)
    case gr: XmlGrammarCompleteResult  => gr.copy(solutionSaved = solSaved)
  }

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: XmlExPart): Either[String, XmlSolution] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(solution) =>
        part match {
          case XmlExParts.GrammarCreationXmlPart  => Right(XmlSolution(document = "", grammar = solution))
          case XmlExParts.DocumentCreationXmlPart => Right(XmlSolution(document = solution, grammar = ""))
        }
      case other              => Left(s"Json was no string but $other")
    }
  }

  override protected def correctEx(
    user: User, solution: XmlSolution, collection: ExerciseCollection, exercise: Exercise, content: XmlExerciseContent, part: XmlExPart
  )(implicit executionContext: ExecutionContext): Future[Try[XmlCompleteResult]] = Future.successful(
    part match {
      case XmlExParts.GrammarCreationXmlPart  => XmlCorrector.correctGrammar(solution, content)
      case XmlExParts.DocumentCreationXmlPart => XmlCorrector.correctDocument(solution,
        solutionDirForExercise(user.username, collection.id, exercise.id).createDirectories(), content)
    }
  )

}
