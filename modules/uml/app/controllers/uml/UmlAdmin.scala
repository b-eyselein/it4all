package controllers.uml

import javax.inject.Inject

import controllers.core.AExerciseAdminController
import model.exercisereading.{JsonReader, ReadingError, ReadingFailure, ReadingResult}
import model.{StringConsts, UmlExTextParser, UmlExercise, UmlExerciseReader}
import play.data.FormFactory
import play.libs.Json
import play.mvc.{Result}
import play.mvc.Results._

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

class UmlAdmin @Inject()(f: FormFactory)
  extends AExerciseAdminController[UmlExercise](f, UmlToolObject, UmlExercise.finder, UmlExerciseReader) {

  def checkSolution: Result = {
    val solNode = Json.parse(factory.form().bindFromRequest().get(StringConsts.SOLUTION_NAME))
    JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      case Success(_) => ok("ok...")
      case Failure(_) => badRequest("FEHLER!")
    }
  }

  def newExerciseStep2: Result = exerciseReader.initFromForm(0, factory.form().bindFromRequest()) match {
    case ReadingError(_, _, _) => badRequest("There has been an error...")
    case ReadingFailure(_) => badRequest("There has been an error...")
    case ReadingResult(exercises) =>
      val exercise = exercises.head.read.asInstanceOf[UmlExercise]
      val parser = new UmlExTextParser(exercise.text, exercise.mappings.asScala.toMap, exercise.ignoreWords.asScala.toList)
      ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser, exercise, parser.capitalizedWords.toList))
  }

  def newExerciseStep3: Result = exerciseReader.initFromForm(0, factory.form().bindFromRequest()) match {
    case ReadingError(_, _, _) => badRequest("There has been an error...")
    case ReadingFailure(_) => badRequest("There has been an error...")
    case ReadingResult(exercises) =>
      val exercise = exercises.head.read.asInstanceOf[UmlExercise]
      ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser, exercise))
  }

}
