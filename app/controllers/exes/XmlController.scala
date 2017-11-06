package controllers.exes

import java.nio.file._
import javax.inject._

import controllers.core.AIdExController
import model.User
import model.core.CommonUtils.RicherTry
import model.core.StringConsts._
import model.core._
import model.core.result.CompleteResult
import model.core.tools.ExerciseOptions
import model.xml._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.{Html, HtmlFormat}

import scala.collection.JavaConverters.{asScalaBufferConverter, seqAsJavaListConverter}
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.{Failure, Try}

object XmlController {

  val STANDARD_XML_PLAYGROUND: String =
    """<?xml version="1.0" encoding="utf-8"?>
      |<!DOCTYPE root [
      |
      |]>""".stripMargin

}

@Singleton
class XmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdExController[XmlExercise, XmlError](cc, dbcp, r, XmlToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = Solution.stringSolForm

  val EX_OPTIONS = ExerciseOptions("Xml", "xml", 10, 20, updatePrev = false)

  // Admin

  override type DbType = XmlExercise

  override val yamlFormat: YamlFormat[XmlExercise] = XmlExYamlProtocol.XmlExYamlFormat

  override type TQ = repo.XmlExerciseTable

  override def tq = repo.xmlExercises

  override def renderEditRest(exercise: Option[XmlExercise]): Html = views.html.xml.editXmlExRest(exercise)

  // User

  override protected def correctEx(sol: StringSolution, exerciseOpt: Option[XmlExercise], user: User): Try[CompleteResult[XmlError]] =
    exerciseOpt match {
      case None => Failure(null)
      case Some(exercise) =>
        checkAndCreateSolDir(user.username, exercise).flatMap(dir => {
          val learnerSolution = sol.learnerSolution

          val (grammarTry, xmlTry) = exercise.exerciseType match {
            case (XmlExType.DTD_XML | XmlExType.XSD_XML) => (
              save(dir, exercise.rootNode + "." + exercise.exerciseType.gramFileEnding, learnerSolution),
              copy(dir, exercise.rootNode + "." + "xml")
            )
            case _ => (
              copy(dir, exercise.rootNode + "." + exercise.exerciseType.gramFileEnding),
              save(dir, exercise.rootNode + "." + "xml", learnerSolution)
            )
          }

          grammarTry.zip(xmlTry).map({ case (grammar, xml) =>
            new CompleteResult(learnerSolution, XmlCorrector.correct(xml, grammar, exercise))
          })
        })
    }

  def playground: EssentialAction = withUser { user => implicit request => Ok(views.html.xml.xmlPlayground.render(user)) }

  def playgroundCorrection = Action { implicit request =>
    val sol = request.body.asFormUrlEncoded.get(FORM_VALUE).mkString("\n")
    Ok(renderResult(new CompleteResult("", XmlCorrector.correct(sol, "", XmlExType.XML_DTD))))
  }

  override def renderExercise(user: User, exercise: XmlExercise): Html = views.html.core.exercise2Rows.render(
    user, XmlToolObject, EX_OPTIONS, exercise, renderExRest(exercise), readDefOrOldSolution(user.username, exercise)
  )

  def renderExRest(exercise: XmlExercise) = new Html(
    s"""<section id="refFileSection">
       |  <pre>${HtmlFormat.escape(getReferenceCode(exercise))}</pre>
       |</section>""".stripMargin)

  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
       |  <a class="btn btn-primary btn-block" href="${controllers.exes.routes.XmlController.playground()}">Xml-Playground</a>
       |</div>
       |<hr>""".stripMargin)

  override def renderResult(completeResult: CompleteResult[XmlError]): Html = completeResult.results match {
    case Nil => new Html("""<div class="alert alert-success">Es wurden keine Fehler gefunden.</div>""")
    case results => new Html(results.map(res =>
      s"""<div class="panel panel-${res.getBSClass}">
         |  <div class="panel-heading">${res.title} ${res.lineStr}</div>
         |  <div class="panel-body">${res.errorMessage}</div>
         |</div>""".stripMargin).mkString("\n"))
  }

  def readDefOrOldSolution(username: String, exercise: XmlExercise): String = Try(
    Files.readAllLines(toolObject.getSolFileForExercise(username, exercise, exercise.rootNode, exercise.exerciseType.studFileEnding)
    ).asScala.mkString("\n")
  ).getOrElse(exercise.fixedStart)

  def copy(dir: Path, filename: String) = Try(Files.copy(Paths.get(toolObject.sampleDir.toString, filename),
    Paths.get(dir.toString, filename), StandardCopyOption.REPLACE_EXISTING))

  def save(dir: Path, filename: String, learnerSolution: String) = Try(
    Files.write(Paths.get(dir.toString, filename), learnerSolution.split(StringConsts.NEWLINE).toList.asJava,
      StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  )

  def getReferenceCode(exercise: XmlExercise): String = {
    val referenceFilePath = Paths.get(toolObject.sampleDir.toString, exercise.rootNode + "." + exercise.exerciseType.refFileEnding)
    Try(Files.readAllLines(referenceFilePath).asScala.mkString("\n")).getOrElse("FEHLER!")
  }

}
