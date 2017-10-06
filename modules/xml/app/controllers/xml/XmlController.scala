package controllers.xml

import java.nio.file.{ Path, Paths }
import java.util.Arrays

import scala.collection.JavaConverters._
import scala.util.Success

import controllers.core.{ BaseController, IdExController }
import javax.inject.Inject
import model.{ CommonUtils, CorrectionException, StringConsts, XmlCorrector, XmlError, XmlExType, XmlExercise }
import model.exercise.ExerciseOptions
import model.result.CompleteResult
import model.user.User
import play.data.{ DynamicForm, FormFactory }
import play.mvc.{ Result, Results }
import model.FailureXmlError
import play.twirl.api.Html

class XmlController @Inject() (f: FormFactory)
  extends IdExController[XmlExercise, XmlError](f, "xml", XmlExercise.finder, XmlToolObject) {

  val EX_OPTIONS = ExerciseOptions("Xml", "xml", 10, 20, false)

  val SAVE_ERROR_MSG = "An error has occured while saving an xml file to "

  override def correctEx(form: DynamicForm, exercise: XmlExercise, user: User) = {
    val learnerSolution = form.get(StringConsts.FORM_VALUE)
    val dir = checkAndCreateSolDir(user.name, exercise)

    val (grammar, xml) = exercise.exerciseType match {
      case (XmlExType.DTD_XML | XmlExType.XSD_XML) ⇒ (saveGrammar(dir, learnerSolution, exercise), saveXML(dir, exercise))
      case _                                       ⇒ (saveGrammar(dir, exercise), saveXML(dir, learnerSolution, exercise))
    }

    Success(new CompleteResult(learnerSolution, XmlCorrector.correct(xml, grammar, exercise).asJava))
  }

  def playground = Results.ok(views.html.xmlPlayground.render(BaseController.getUser))

  def playgroundCorrection: Result = {
    val result = new CompleteResult("", XmlCorrector.correct(
      factory.form().bindFromRequest().get(StringConsts.FORM_VALUE), "", XmlExType.XML_DTD).asJava)
    Results.ok(renderResult(result))
  }

  override def renderExercise(user: User, exercise: XmlExercise) =
    views.html.exercise2Rows.render(user, toolObject, EX_OPTIONS, exercise,
                                    views.html.xmlExRest.render(exercise), readDefOrOldSolution(user.name, exercise))

  override def renderExesListRest = views.html.xmlExesListRest.render

  override def renderResult(completeResult: CompleteResult[XmlError]) = completeResult.results.asScala.toList match {
    case Nil ⇒ new Html("""<div class="alert alert-success">Es wurden keine Fehler gefunden.</div>""")
    case results ⇒ new Html(results.map(res ⇒ s"""
<div class="panel panel-${res.getBSClass}">
  <div class="panel-heading">${res.title} ${res.lineStr}</div>
  <div class="panel-body">${res.errorMessage}</div>
</div>""").mkString("\n"))
  }

  def readDefOrOldSolution(username: String, exercise: XmlExercise) = {
    val oldSolutionPath = BaseController.getSolFileForExercise(username, exType, exercise, exercise.rootNode,
                                                               exercise.getStudentFileEnding)
    if (oldSolutionPath.toFile.exists) CommonUtils.readFile(oldSolutionPath) else exercise.getFixedStart
  }

  def saveGrammar(dir: Path, learnerSolution: String, exercise: XmlExercise) = {
    //    final Path grammar = Paths.get(dir.toString(), exercise.rootNode + exercise.getGrammarFileEnding())
    //
    //    try {
    //      return Files.write(grammar, Arrays.asList(learnerSolution.split(StringConsts.NEWLINE)), StandardOpenOption.CREATE,
    //          StandardOpenOption.TRUNCATE_EXISTING)
    //    } catch (final IOException error) {
    //      Logger.error("Fehler beim Speichern einer Xml-Loesungsdatei!", error)
    null
    //    }
  }

  def saveGrammar(dir: Path, exercise: XmlExercise) = {
    //    final String filename = exercise.rootNode + "." + exercise.getGrammarFileEnding()
    //
    //    final Path target = Paths.get(dir.toString(), filename)
    //    final Path source = Paths.get(getSampleDir().toString(), filename)
    //
    //    try {
    //      return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
    //    } catch (final IOException error) {
    //      Logger.error(SAVE_ERROR_MSG + target.toString(), error)
    null
    //    }
  }

  def saveXML(dir: Path, learnerSolution: String, exercise: XmlExercise) = {
    //    final Path targetFile = Paths.get(dir.toString(), exercise.rootNode + ".xml")
    //
    //    try {
    //      return Files.write(targetFile, Arrays.asList(learnerSolution.split(StringConsts.NEWLINE)),
    //          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    //    } catch (final IOException error) {
    //      Logger.error(SAVE_ERROR_MSG + targetFile.toString(), error)
    null
    //    }
  }

  def saveXML(dir: Path, exercise: XmlExercise) = {
    //    final String filename = exercise.rootNode + ".xml"
    //
    //    final Path target = Paths.get(dir.toString(), filename)
    //    final Path source = Paths.get(getSampleDir().toString(), filename)
    //
    //    try {
    //      return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
    //    } catch (final IOException error) {
    //      Logger.error(SAVE_ERROR_MSG + target.toString(), error)
    null
    //    }
  }

}

object XmlController {

  val STANDARD_XML_PLAYGROUND = """<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE root [

]>"""

  def getReferenceCode(exercise: XmlExercise) = {
    val referenceFilePath = Paths.get(
      BaseController.getSampleDir("xml").toString, exercise.rootNode + "." + exercise.getReferenceFileEnding)
    if (referenceFilePath.toFile.exists) CommonUtils.readFile(referenceFilePath) else "FEHLER!"
  }

}
