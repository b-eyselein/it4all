package model.tools.xml

import javax.inject._
import model._
import model.core.result.CompleteResultJsonProtocol
import model.points.Points
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.xml.persistence.XmlTableDefs
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{Format, JsString}
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try


@Singleton
class XmlToolMain @Inject()(val tables: XmlTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Xml", "xml") {

  // Result types

  override type PartType = XmlExPart
  override type ExType = XmlExercise

  override type SolType = XmlSolution
  override type SampleSolType = XmlSampleSolution
  override type UserSolType = XmlUserSolution

  override type ReviewType = XmlExerciseReview

  override type ResultType = XmlEvaluationResult
  override type CompResultType = XmlCompleteResult

  override type Tables = XmlTableDefs

  // Other members

  override val hasPlayground = true

  override val toolState: ToolState = ToolState.LIVE

  override val exParts: Seq[XmlExPart] = XmlExParts.values

  // Yaml, Html forms, Json

  override protected val exerciseYamlFormat: MyYamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  override val exerciseJsonFormat: Format[XmlExercise] = XmlCompleteResultJsonProtocol.exerciseFormat

  override val exerciseReviewForm: Form[XmlExerciseReview] = XmlToolForms.exerciseReviewForm

  // FIXME: do not use anymore ?!?
  override val sampleSolutionJsonFormat: Format[XmlSampleSolution] = XmlCompleteResultJsonProtocol.xmlSampleSolutionJsonFormat

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[XmlEvaluationResult, XmlCompleteResult] = XmlCompleteResultJsonProtocol

  // Other helper methods

  override def futureUserCanSolveExPart(username: String, collId: Int, exId: Int, part: XmlExPart): Future[Boolean] = part match {
    case XmlExParts.GrammarCreationXmlPart  => Future.successful(true)
    case XmlExParts.DocumentCreationXmlPart => futureMaybeOldSolution(username, collId, exId, XmlExParts.GrammarCreationXmlPart).map(_.exists(r => r.points == r.maxPoints))
  }

  override def instantiateSolution(id: Int, exercise: XmlExercise, part: XmlExPart, solution: XmlSolution, points: Points, maxPoints: Points): XmlUserSolution =
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
      case other              => Left(s"Json was no string but ${other}")
    }
  }

  override protected def correctEx(user: User, solution: XmlSolution, collection: ExerciseCollection, exercise: XmlExercise,
                                   part: XmlExPart): Future[Try[XmlCompleteResult]] = Future.successful(
    part match {
      case XmlExParts.GrammarCreationXmlPart  => XmlCorrector.correctGrammar(solution, exercise)
      case XmlExParts.DocumentCreationXmlPart => XmlCorrector.correctDocument(solution,
        solutionDirForExercise(user.username, collection.id, exercise.id).createDirectories(), exercise)
    }
  )


  // Views

  override def renderExercise(user: User, collection: ExerciseCollection, exercise: XmlExercise, part: XmlExPart, maybeOldSolution: Option[XmlUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    val oldSolutionOrTemplate: XmlSolution = maybeOldSolution.map(_.solution).getOrElse(exercise.getTemplate(part))

    views.html.toolViews.xml.xmlExercise(user, collection, exercise, oldSolutionOrTemplate, part, this)
  }

  override def playground(user: User): Html = views.html.toolViews.xml.xmlPlayground(user)

}
