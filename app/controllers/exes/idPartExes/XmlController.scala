package controllers.exes.idPartExes

import java.nio.file._
import javax.inject._

import controllers.Secured
import controllers.exes.idPartExes.XmlController._
import model.User
import model.core._
import model.core.tools.ExerciseOptions
import model.xml.XmlConsts._
import model.xml.XmlEnums._
import model.xml._
import model.yaml.MyYamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import views.html.xml._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}
import scalatags.Text.all._

object XmlController {

  val exOptions = ExerciseOptions("Xml", "xml", 15, 30, updatePrev = false)

}

@Singleton
class XmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: XmlTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[XmlExercise, XmlExercise, XmlExPart, XmlError, XmlCompleteResult, XmlTableDefs](cc, dbcp, t, XmlToolObject) with Secured with FileUtils {

  override protected def partTypeFromUrl(urlName: String): Option[XmlExPart] = XmlExParts.values find (_.urlName == urlName)

  // Reading solution from requests

  override type SolType = XmlSolution

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[XmlSolution] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(XmlDocumentSolution(id, user.username, sol.learnerSolution)))

  override protected def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: XmlExPart): Option[XmlSolution] = part match {
    case GrammarCreationXmlPart  => jsValue.asStr map (XmlGrammarSolution(id, user.username, _))
    case DocumentCreationXmlPart => jsValue.asStr map (XmlDocumentSolution(id, user.username, _))
  }

  // Yaml

  override val yamlFormat: MyYamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // db

  // Other routes

  def playground: EssentialAction = withUser { user => implicit request => Ok(xmlPlayground(user)) }

  def playgroundCorrection = Action { implicit request =>
    Solution.stringSolForm.bindFromRequest.fold(
      _ => BadRequest("There has been an error!"),
      sol => {
        val correctionResult = XmlCorrector.correct(sol.learnerSolution, "", XmlExType.XML_DTD)
        val result = XmlCompleteResult(sol.learnerSolution, solutionSaved = false, correctionResult)
        Ok(result.render)
      })
  }

  // Correction

  override protected def correctEx(user: User, solution: XmlSolution, completeEx: XmlExercise): Future[Try[XmlCompleteResult]] = tables.saveXmlSolution(solution) map { solSaved =>

    solution match {
      case xds: XmlDocumentSolution =>
        checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {

          val grammarAndXmlTries: Try[(Path, Path)] = if (completeEx.exerciseType == XmlExType.DTD_XML || completeEx.exerciseType == XmlExType.XSD_XML) {
            for {
              grammar <- write(dir, completeEx.rootNode + "." + completeEx.exerciseType.gramFileEnding, xds.solution)
              xml <- write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, completeEx.refFileContent)
            } yield (grammar, xml)
          } else {
            for {
              grammar <- write(dir, completeEx.rootNode + "." + completeEx.exerciseType.gramFileEnding, completeEx.refFileContent)
              xml <- write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, xds.solution)
            } yield (grammar, xml)
          }

          grammarAndXmlTries map { case (grammar, xml) =>
            val correctionResult = XmlCorrector.correct(xml, grammar, completeEx.exerciseType)
            XmlCompleteResult(xds.solution, solSaved, correctionResult)
          }
        })

      case xgs: XmlGrammarSolution =>
        XmlCorrector.correctDTD(xgs.solution, completeEx)
        Failure(new Exception("Not implemented yet: correction of grammar..."))
    }
  }

  // Views

  override def renderEditRest(exercise: Option[XmlExercise]): Html = editXmlExRest(exercise)

  override def renderExesListRest: Html = new Html(a(cls := "btn btn-primary btn-block", href := routes.XmlController.playground().url)("Xml-Playground").toString() + "<hr>")

  override protected def renderExercise(user: User, exercise: XmlExercise, part: XmlExPart): Future[Html] = tables.readXmlSolution(user.username, exercise.ex.id, part) map {
    maybeOldSolution =>
      val template = maybeOldSolution getOrElse exercise.getTemplate(part)
      views.html.core.exercise2Rows(user, toolObject, exOptions, exercise.ex, renderExRest(exercise.ex, _), exScript, template, part)
  }

  private def exScript: Html = Html(script(src := controllers.routes.Assets.versioned("javascripts/xml/xmlExercise.js").url).toString)

  def renderExRest(exercise: XmlExercise, urlName: String): Html = XmlExParts.values.find(_.urlName == urlName) match {
    case (None | Some(DocumentCreationXmlPart)) => Html(div(id := "refFileSection")(pre(exercise.refFileContent)).toString)
    case Some(GrammarCreationXmlPart)           => Html(p(exercise.grammarDescription).toString)
  }

  // Result handlers

  override protected def onSubmitCorrectionResult(user: User, result: XmlCompleteResult): Result =
    Ok(views.html.core.correction.render(result, result.render, user, toolObject))

  override protected def onSubmitCorrectionError(user: User, error: CorrectionException): Result = ???

  override protected def onLiveCorrectionResult(result: XmlCompleteResult): Result = Ok(result.toJson)

  override protected def onLiveCorrectionError(error: CorrectionException): Result = error match {
    case NoSuchExerciseException(notExistingId)   => BadRequest(Json.obj("msg" -> s"Es gibt keine Aufgabe mit der ID '$notExistingId'!"))
    case SolutionTransferException()              => BadRequest(Json.obj("msg" -> "Es gab einen Fehler bei der Übertragung ihrer Lösung!"))
    case OtherCorrectionException(otherException) =>
      BadRequest(Json.obj(
        "msg" -> ("Es gab einen anderen Fehler bei der Korrektur ihrer Lösung:\n" + otherException.getMessage)
      ))
  }

}