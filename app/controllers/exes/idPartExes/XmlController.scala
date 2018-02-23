package controllers.exes.idPartExes

import java.nio.file._

import controllers.exes.ExerciseOptions
import controllers.exes.idPartExes.XmlController._
import javax.inject._
import model.core._
import model.xml.XmlConsts._
import model.xml._
import model.yaml.MyYamlFormat
import model.{Consts, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html
import scalatags.Text.all._
import views.html.xml._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Try}

object XmlController {

  val exOptions = ExerciseOptions("Xml", "xml", 15, 30, updatePrev = false)

}

@Singleton
class XmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val tables: XmlTableDefs)(implicit ec: ExecutionContext)
  extends FileUtils with AIdPartExToolMain[XmlExPart, XmlSolution, XmlExercise, XmlExercise] {

  override val exType  : String = "xml"
  override val toolname: String = "Xml"
  override val urlPart : String = "xml"
  override val consts  : Consts = XmlConsts

  override def partTypeFromUrl(urlName: String): Option[XmlExPart] = XmlExParts.values find (_.urlName == urlName)

  // Result types

  override type Tables = XmlTableDefs

  override type R = XmlError

  override type CompResult = XmlCompleteResult

  // Reading solution from requests, saving

  //  override type SolType = XmlSolution

  override def saveSolution(sol: XmlSolution): Future[Boolean] = tables.saveXmlSolution(sol)

  override def readOldSolution(user: User, exerciseId: Int, part: XmlExPart): Future[Option[XmlSolution]] =
    tables.readXmlSolution(user.username, exerciseId, part)

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[XmlSolution] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(XmlDocumentSolution(id, user.username, sol.learnerSolution)))

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: XmlExPart): Option[XmlSolution] = part match {
    case GrammarCreationXmlPart  => jsValue.asStr map (XmlGrammarSolution(id, user.username, _))
    case DocumentCreationXmlPart => jsValue.asStr map (XmlDocumentSolution(id, user.username, _))
  }

  // Yaml

  override val yamlFormat: MyYamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // Correction

  override protected def correctEx(user: User, solution: XmlSolution, completeEx: XmlExercise): Future[Try[XmlCompleteResult]] = saveSolution(solution) map { solSaved =>

    solution match {
      case xds: XmlDocumentSolution =>
        checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {

          val grammarAndXmlTries: Try[(Path, Path)] = for {
            grammar <- write(dir, completeEx.rootNode + ".dtd", completeEx.sampleGrammar)
            xml <- write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, xds.solution)
          } yield (grammar, xml)

          grammarAndXmlTries map { case (grammar, xml) =>
            val correctionResult = XmlCorrector.correct(xml, grammar)
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

  //  override def renderExesListRest: Html = new Html(a(cls := "btn btn-primary btn-block", href := routes.XmlController.xmlPlayground().url)("Xml-Playground").toString() + "<hr>")

  override def renderExercise(user: User, exercise: XmlExercise, maybePart: Option[XmlExPart]): Future[Html] =
    maybePart match {
      case Some(part) =>
        readOldSolution(user, exercise.ex.id, part) map {
          maybeOldSolution =>
            val template = maybeOldSolution map (_.solution) getOrElse exercise.getTemplate(part)

            val exScript: Html = Html(script(src := controllers.routes.Assets.versioned("javascripts/xml/xmlExercise.js").url).toString)

            val exRest = part match {
              case DocumentCreationXmlPart => Html(pre(exercise.sampleGrammar).toString)
              case GrammarCreationXmlPart  => Html(div(cls := "well")(exercise.grammarDescription).toString)
            }

            views.html.core.exercise2Rows(user, this, exOptions, exercise.ex, exRest, exScript, template, part)
        }
      case None       => ???
    }

  // Result handlers

  override def onSubmitCorrectionResult(user: User, result: XmlCompleteResult): Html =
    views.html.core.correction.render(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: XmlCompleteResult): JsValue = result.toJson

}