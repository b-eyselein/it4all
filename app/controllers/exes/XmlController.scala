package controllers.exes

import java.nio.file._
import javax.inject._

import controllers.Secured
import controllers.core.AIdExController
import controllers.exes.XmlController._
import model.User
import model.core.CommonUtils.RicherTry
import model.core._
import model.core.tools.ExerciseOptions
import model.xml._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.{Html, HtmlFormat}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object XmlController {

  val XML_FILE_ENDING = "xml"

  val EX_OPTIONS = ExerciseOptions("Xml", "xml", 15, 30, updatePrev = false)

}

@Singleton
class XmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[XmlExercise, XmlError](cc, dbcp, r, XmlToolObject) with Secured with FileUtils {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = Solution.stringSolForm

  // Yaml

  override type CompEx = XmlExercise

  override val yamlFormat: YamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  // db

  import profile.api._

  override type TQ = repo.XmlExerciseTable

  override def tq = repo.xmlExercises

  override def completeExes: Future[Seq[XmlExercise]] = db.run(repo.xmlExercises.result)

  override def completeExById(id: Int): Future[Option[XmlExercise]] =
    db.run(repo.xmlExercises.findBy(_.id).apply(id).result.headOption)

  override def saveRead(read: Seq[XmlExercise]): Future[Seq[Int]] = Future.sequence(read.map(completeEx =>
    db.run(repo.xmlExercises insertOrUpdate completeEx.ex)))

  // Other routes

  def playground: EssentialAction = withUser { user => implicit request => Ok(views.html.xml.xmlPlayground.render(user)) }

  def playgroundCorrection = Action { implicit request =>
    solForm.bindFromRequest.fold(
      _ => BadRequest("There has been an error!"),
      sol => Ok(renderResult(new CompleteResult(sol.learnerSolution, XmlCorrector.correct(sol.learnerSolution, "", XmlExType.XML_DTD))))
    )
  }

  // Correction

  override protected def correctEx(sol: StringSolution, completeEx: XmlExercise, user: User): Try[CompleteResult[XmlError]] =
    checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {
      val (grammarTry: Try[Path], xmlTry: Try[Path]) =
        if (completeEx.exerciseType == XmlExType.DTD_XML || completeEx.exerciseType == XmlExType.XSD_XML) {
          val grammar = write(dir, completeEx.rootNode + "." + completeEx.exerciseType.gramFileEnding, sol.learnerSolution)
          val xml = write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, completeEx.refFileContent)
          (grammar, xml)
        } else {
          val grammar = write(dir, completeEx.rootNode + "." + completeEx.exerciseType.gramFileEnding, completeEx.refFileContent)
          val xml = write(dir, completeEx.rootNode + "." + XML_FILE_ENDING, sol.learnerSolution)
          (grammar, xml)
        }

      grammarTry.zip(xmlTry).map {
        case (grammar, xml) => new CompleteResult(sol.learnerSolution, XmlCorrector.correct(xml, grammar, completeEx))
      }
    })

  // Views

  override def renderEditRest(exercise: Option[XmlExercise]): Html = views.html.xml.editXmlExRest(exercise)

  override def renderExesListRest: Html = new Html(
    s"""<a class="btn btn-primary btn-block" href="${controllers.exes.routes.XmlController.playground()}">Xml-Playground</a>
       |<hr>""".stripMargin)

  override def renderResult(completeResult: CompleteResult[XmlError]): Html = new Html(completeResult.results match {
    case Nil     => """<div class="alert alert-success"><span class="glyphicon glyphicon-ok"></span> Es wurden keine Fehler gefunden.</div>"""
    case results => results map (_.render) mkString "\n"
  })

  override def renderExercise(user: User, exercise: XmlExercise): Html = views.html.core.exercise2Rows.render(
    user, XmlToolObject, EX_OPTIONS, exercise.ex, renderExRest(exercise.ex), readDefOrOldSolution(user.username, exercise.ex))

  def renderExRest(exercise: XmlExercise) = new Html(
    s"""<section id="refFileSection">
       |  <pre>${HtmlFormat.escape(exercise.refFileContent)}</pre>
       |</section>""".stripMargin)

  // Own functions

  // FIXME: read old xml solution from db!
  private def readDefOrOldSolution(username: String, exercise: XmlExercise): String =
    readAll(toolObject.getSolFileForExercise(username, exercise, exercise.rootNode, exercise.exerciseType.studFileEnding)).getOrElse(exercise.fixedStart)

}