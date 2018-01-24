package controllers.exes.idPartExes

import java.nio.file._
import javax.inject._

import controllers.Secured
import controllers.exes.idPartExes.XmlController._
import model.User
import model.core.CommonUtils.RicherTry
import model.core._
import model.core.tools.ExerciseOptions
import model.xml.XmlConsts._
import model.xml.XmlEnums._
import model.xml.XmlExParts.XmlExPart
import model.xml._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.{Html, HtmlFormat}
import views.html.xml._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try
import scalatags.Text.all._

object XmlController {

  val exOptions = ExerciseOptions("Xml", "xml", 15, 30, updatePrev = false)

}

@Singleton
class XmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: XmlTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[XmlExercise, XmlExercise, XmlError, XmlCompleteResult, XmlTableDefs](cc, dbcp, t, XmlToolObject) with Secured with FileUtils {

  override type PartType = XmlExPart

  override protected def partTypeFromUrl(urlName: String): Option[XmlExPart] = Some(XmlExParts.XmlSingleExPart)

  // Reading solution from requests

  override type SolType = XmlSolution

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[XmlSolution] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(XmlSolution(id, user.username, sol.learnerSolution)))

  override protected def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: XmlExPart): Option[XmlSolution] =
    jsValue.asStr map (XmlSolution(id, user.username, _))

  // Yaml

  override val yamlFormat: YamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // db

  // Other routes

  def playground: EssentialAction = withUser { user => implicit request => Ok(xmlPlayground(user)) }

  def playgroundCorrection = Action { implicit request =>
    Solution.stringSolForm.bindFromRequest.fold(
      _ => BadRequest("There has been an error!"),
      sol => {
        val correctionResult = XmlCorrector.correct(sol.learnerSolution, "", XmlExType.XML_DTD)
        Ok(renderResult(XmlCompleteResult(sol.learnerSolution, solutionSaved = false, correctionResult)))
      })
  }

  // Correction

  override protected def correctEx(user: User, solution: XmlSolution, completeEx: XmlExercise): Future[Try[XmlCompleteResult]] = tables.saveXmlSolution(solution) map { solSaved =>

    checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {

      val (grammarTry: Try[Path], xmlTry: Try[Path]) = if (completeEx.exerciseType == XmlExType.DTD_XML || completeEx.exerciseType == XmlExType.XSD_XML) {
        val grammar = write(dir, completeEx.rootNode + "." + completeEx.exerciseType.gramFileEnding, solution.solution)
        val xml = write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, completeEx.refFileContent)
        (grammar, xml)
      } else {
        val grammar = write(dir, completeEx.rootNode + "." + completeEx.exerciseType.gramFileEnding, completeEx.refFileContent)
        val xml = write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, solution.solution)
        (grammar, xml)
      }

      (grammarTry zip xmlTry) map { case (grammar, xml) =>
        val correctionResult = XmlCorrector.correct(xml, grammar, completeEx.exerciseType)
        XmlCompleteResult(solution.solution, solSaved, correctionResult)
      }
    })
  }

  // Views

  override def renderEditRest(exercise: Option[XmlExercise]): Html = editXmlExRest(exercise)

  override def renderExesListRest: Html = new Html(a(cls := "btn btn-primary btn-block", href := routes.XmlController.playground().url)("Xml-Playground").toString() + "<hr>")

  private def renderResult(completeResult: XmlCompleteResult): Html = completeResult.render

  override protected def renderExercise(user: User, exercise: XmlExercise, part: XmlExPart): Future[Html] = readDefOrOldSolution(user.username, exercise.ex) map {
    sol => views.html.core.exercise2Rows(user, XmlToolObject, exOptions, exercise.ex, renderExRest(exercise.ex), exScript, sol, XmlExParts.XmlSingleExPart)
  }

  private def exScript: Html = Html(script(src := controllers.routes.Assets.versioned("javascripts/xml/xmlExercise.js").url).toString)

  def renderExRest(exercise: XmlExercise) = Html(div(id := "refFileSection")(pre(HtmlFormat.escape(exercise.refFileContent).toString)).toString)

  // Own functions

  private def readDefOrOldSolution(username: String, exercise: XmlExercise): Future[String] =
    tables.readXmlSolution(username, exercise.id).map(_.map(_.solution) getOrElse exercise.fixedStart)

  override protected def onSubmitCorrectionResult(user: User, result: XmlCompleteResult): Result =
    Ok(views.html.core.correction.render(result, renderResult(result), user, toolObject))

  override protected def onSubmitCorrectionError(user: User, error: Throwable): Result = ???

  override protected def onLiveCorrectionResult(result: XmlCompleteResult): Result = Ok(renderResult(result))

  override protected def onLiveCorrectionError(error: Throwable): Result = ???

}