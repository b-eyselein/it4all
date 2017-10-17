package controllers.xml

import java.nio.file._
import javax.inject.Inject

import controllers.core.IdExController
import model.CommonUtils.RicherTry
import model._
import model.exercise.ExerciseOptions
import model.result.CompleteResult
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.mvc.{Result, Results}
import play.twirl.api.{Html, HtmlFormat}

import scala.collection.JavaConverters.{asScalaBufferConverter, seqAsJavaListConverter}
import scala.util.Try

class XmlAdmin @javax.inject.Inject()(f: play.data.FormFactory)
  extends controllers.core.AExerciseAdminController[XmlExercise](f, XmlToolObject, XmlExercise.finder, model.XmlExerciseReader) {

  override def statistics = new Html(
    s"""<li>Es existieren insgesamt ${XmlExercise.finder.all.size} <a href="${controllers.xml.routes.XmlAdmin.exercises()}">Aufgaben</a>, davon
       |  <ul>
       |    ${XmlExercise.finder.all.asScala.groupBy(_.exerciseType).map({ case (exType, exes) => s"""<li>${exes.size} Aufgaben von Typ $exType""" }).mkString("\n")}
       |  </ul>
       |</li>""".stripMargin)
}

class XmlController @Inject()(f: FormFactory)
  extends IdExController[XmlExercise, XmlError](f, XmlExercise.finder, XmlToolObject) {

  val EX_OPTIONS = ExerciseOptions("Xml", "xml", 10, 20, updatePrev = false)

  val SAVE_ERROR_MSG = "An error has occured while saving an xml file to "

  override protected def correctEx(form: DynamicForm, exercise: XmlExercise, user: User): Try[CompleteResult[XmlError]] =
    checkAndCreateSolDir(user.name, exercise).flatMap(dir => {
      val learnerSolution = form.get(StringConsts.FORM_VALUE)

      val (grammarTry, xmlTry) = exercise.exerciseType match {
        case (DTD_XML | XSD_XML) => (
          save(dir, exercise.rootNode + "." + exercise.exerciseType.gramFileEnding, learnerSolution),
          copy(dir, exercise.rootNode + "." + "xml")
        )
        case _ => (
          copy(dir, exercise.rootNode + "." + exercise.exerciseType.gramFileEnding),
          save(dir, exercise.rootNode + "." + "xml", learnerSolution)
        )
      }

      grammarTry.zip(xmlTry).map({ case (grammar, xml) =>
        new CompleteResult(learnerSolution, XmlCorrector.correct(xml, grammar, exercise).asJava)
      })
    })


  def playground: Result = Results.ok(views.html.xmlPlayground.render(getUser))

  def playgroundCorrection: Result = Results.ok(
    renderResult(
      new CompleteResult("", XmlCorrector.correct(factory.form().bindFromRequest().get(StringConsts.FORM_VALUE), "", XML_DTD).asJava)
    )
  )

  override def renderExercise(user: User, exercise: XmlExercise): Html = views.html.exercise2Rows.render(
    user, toolObject, EX_OPTIONS, exercise, renderExRest(exercise), readDefOrOldSolution(user.name, exercise)
  )

  def renderExRest(exercise: XmlExercise) = new Html(
    s"""<section id="refFileSection">
       |  <pre>${HtmlFormat.escape(getReferenceCode(exercise))}</pre>
       |</section>
""".stripMargin)

  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
       |  <a class="btn btn-primary btn-block" href="${controllers.xml.routes.XmlController.playground()}">Xml-Playground</a>
       |</div>

       |<hr>""".stripMargin)

  override def renderResult(completeResult: CompleteResult[XmlError]): Html = completeResult.results.asScala.toList match {
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

object XmlController {

  val STANDARD_XML_PLAYGROUND: String =
    """<?xml version="1.0" encoding="utf-8"?>
      |<!DOCTYPE root [
      |
      |]>""".stripMargin

}
