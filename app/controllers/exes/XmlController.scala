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
import model.xml.XmlConsts._
import model.xml._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.{Html, HtmlFormat}

import scala.collection.JavaConverters.{asScalaBufferConverter, seqAsJavaListConverter}
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object XmlController {

  val EX_OPTIONS = ExerciseOptions("Xml", "xml", 15, 30, updatePrev = false)

}

@Singleton
class XmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[XmlExercise, XmlError](cc, dbcp, r, XmlToolObject) with Secured {

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

  override def completeExById(id: Int): Future[Option[XmlExercise]] = db.run(repo.xmlExercises.findBy(_.id).apply(id).result.headOption)

  override def saveRead(read: Seq[XmlExercise]): Future[Seq[Int]] = Future.sequence(read.map(completeEx =>
    db.run(repo.xmlExercises insertOrUpdate completeEx.ex)))

  // Other routes

  def playground: EssentialAction = withUser { user => implicit request => Ok(views.html.xml.xmlPlayground.render(user)) }

  def playgroundCorrection = Action { implicit request =>
    val sol = request.body.asFormUrlEncoded.get(FORM_VALUE).mkString("\n")
    Ok(renderResult(new CompleteResult("", XmlCorrector.correct(sol, "", XmlExType.XML_DTD))))
  }

  // Views

  override def renderEditRest(exercise: Option[XmlExercise]): Html = views.html.xml.editXmlExRest(exercise)

  override def renderExesListRest: Html = new Html(
    s"""<div class="panel panel-default">
       |  <a class="btn btn-primary btn-block" href="${controllers.exes.routes.XmlController.playground()}">Xml-Playground</a>
       |</div>
       |<hr>""".stripMargin)

  override def renderResult(completeResult: CompleteResult[XmlError]): Html = new Html(completeResult.results match {
    case Nil     => """<div class="alert alert-success">Es wurden keine Fehler gefunden.</div>"""
    case results => results.map(res =>
      s"""<div class="panel panel-${res.getBSClass}">
         |  <div class="panel-heading">${res.title} ${res.lineStr}</div>
         |  <div class="panel-body">${res.errorMessage}</div>
         |</div>""".stripMargin).mkString("\n")
  })

  override def renderExercise(user: User, exercise: XmlExercise): Html = views.html.core.exercise2Rows.render(
    user, XmlToolObject, EX_OPTIONS, exercise.ex, renderExRest(exercise.ex), readDefOrOldSolution(user.username, exercise.ex))

  def renderExRest(exercise: XmlExercise) = new Html(
    s"""<section id="refFileSection">
       |  <pre>${HtmlFormat.escape(exercise.refFileContent)}</pre>
       |</section>""".stripMargin)

  // Correction

  override protected def correctEx(sol: StringSolution, completeEx: XmlExercise, user: User): Try[CompleteResult[XmlError]] = {
    checkAndCreateSolDir(user.username, completeEx) flatMap (dir => {
      val exercise = completeEx.ex
      val learnerSolution = sol.learnerSolution

      val (grammarTry: Try[Path], xmlTry: Try[Path]) =
        if (exercise.exerciseType == XmlExType.DTD_XML || exercise.exerciseType == XmlExType.XSD_XML) {
          (save(dir, exercise.rootNode + "." + exercise.exerciseType.gramFileEnding, learnerSolution),
            save(dir, exercise.rootNode + "." + "xml", exercise.refFileContent))
        } else {
          (save(dir, exercise.rootNode + "." + exercise.exerciseType.gramFileEnding, exercise.refFileContent),
            save(dir, exercise.rootNode + "." + "xml", learnerSolution))
        }

      grammarTry.zip(xmlTry).map {
        case (grammar, xml) => new CompleteResult(learnerSolution, XmlCorrector.correct(xml, grammar, exercise))
      }
    })
  }

  // Own functions

  // FIXME: read old xml solution from db!
  private def readDefOrOldSolution(username: String, exercise: XmlExercise): String = Try(
    Files.readAllLines(toolObject.getSolFileForExercise(username, exercise, exercise.rootNode, exercise.exerciseType.studFileEnding)
    ).asScala.mkString("\n")
  ).getOrElse(exercise.fixedStart)

  // FIXME: write in other way?
  private def save(dir: Path, filename: String, learnerSolution: String): Try[Path] = Try(
    Files.write(Paths.get(dir.toString, filename), learnerSolution.split(NEWLINE).toList.asJava,
      StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  )

}
