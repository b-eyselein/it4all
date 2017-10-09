package controllers.xml

import java.nio.file.{ Files, Path, Paths, StandardCopyOption, StandardOpenOption }

import scala.collection.JavaConverters.{ asScalaBufferConverter, seqAsJavaListConverter }
import scala.util.Try

import controllers.core.{ BaseController, IdExController }
import javax.inject.Inject
import model.{ StringConsts, XmlCorrector, XmlError, XmlExType, XmlExercise }
import model.CommonUtils.RicherTry
import model.exercise.ExerciseOptions
import model.result.CompleteResult
import model.user.User
import play.data.{ DynamicForm, FormFactory }
import play.mvc.Results
import play.twirl.api.Html
import play.twirl.api.HtmlFormat

class XmlController @Inject() (f: FormFactory)
  extends IdExController[XmlExercise, XmlError](f, "xml", XmlExercise.finder, XmlToolObject) {

  val EX_OPTIONS = ExerciseOptions("Xml", "xml", 10, 20, false)

  val SAVE_ERROR_MSG = "An error has occured while saving an xml file to "

  override def correctEx(form: DynamicForm, exercise: XmlExercise, user: User) = {
    val learnerSolution = form.get(StringConsts.FORM_VALUE)
    val dir = checkAndCreateSolDir(user.name, exercise)

    val (grammarTry, xmlTry) = exercise.exerciseType match {
      case (XmlExType.DTD_XML | XmlExType.XSD_XML) => (
        save(dir, exercise.rootNode + exercise.getGrammarFileEnding, learnerSolution),
        copy(dir, exercise.rootNode + ".xml")
      )
      case _ => (
        copy(dir, exercise.rootNode + "." + exercise.getGrammarFileEnding),
        save(dir, exercise.rootNode + ".xml", learnerSolution)
      )
    }

    grammarTry.zip(xmlTry).map({
      case (grammar, xml) =>
        new CompleteResult(learnerSolution, XmlCorrector.correct(xml, grammar, exercise).asJava)
    })
  }

  def playground = Results.ok(views.html.xmlPlayground.render(BaseController.getUser))

  def playgroundCorrection = Results.ok(
    renderResult(
      new CompleteResult(
        "",
        XmlCorrector.correct(factory.form().bindFromRequest().get(StringConsts.FORM_VALUE), "", XmlExType.XML_DTD).asJava
      )
    )
  )

  override def renderExercise(user: User, exercise: XmlExercise) = views.html.exercise2Rows.render(
    user, toolObject, EX_OPTIONS, exercise, renderExRest(exercise), readDefOrOldSolution(user.name, exercise)
  )

  def renderExRest(exercise: XmlExercise) = new Html(s"""<section id="refFileSection">
  <pre>${HtmlFormat.escape(XmlController.getReferenceCode(exercise))}</pre>
</section>
""")

  override def renderExesListRest = new Html(s"""<div class="panel panel-default">
  <a class="btn btn-primary btn-block" href="${controllers.xml.routes.XmlController.playground}">Xml-Playground</a>
</div>

<hr>""")

  override def renderResult(completeResult: CompleteResult[XmlError]) = completeResult.results.asScala.toList match {
    case Nil => new Html("""<div class="alert alert-success">Es wurden keine Fehler gefunden.</div>""")
    case results => new Html(results.map(res => s"""
<div class="panel panel-${res.getBSClass}">
  <div class="panel-heading">${res.title} ${res.lineStr}</div>
  <div class="panel-body">${res.errorMessage}</div>
</div>""").mkString("\n"))
  }

  def readDefOrOldSolution(username: String, exercise: XmlExercise) = Try(
    Files.readAllLines(
      BaseController.getSolFileForExercise(username, exType, exercise, exercise.rootNode, exercise.getStudentFileEnding)
    ).asScala.mkString("\n")
  ).getOrElse(exercise.getFixedStart)

  def copy(dir: Path, filename: String) = Try(
    Files.copy(
      Paths.get(getSampleDir.toString, filename),
      Paths.get(dir.toString, filename),
      StandardCopyOption.REPLACE_EXISTING
    )
  )

  def save(dir: Path, filename: String, learnerSolution: String) = Try(
    Files.write(
      Paths.get(dir.toString, filename),
      learnerSolution.split(StringConsts.NEWLINE).toList.asJava,
      StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
    )
  )

}

object XmlController {

  val STANDARD_XML_PLAYGROUND = """<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE root [

]>"""

  def getReferenceCode(exercise: XmlExercise) = {
    val referenceFilePath = Paths.get(
      BaseController.getSampleDir("xml").toString, exercise.rootNode + "." + exercise.getReferenceFileEnding
    )
    Try(Files.readAllLines(referenceFilePath).asScala.mkString("\n")).getOrElse("FEHLER!")
  }

}