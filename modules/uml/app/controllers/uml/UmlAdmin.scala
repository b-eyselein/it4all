package controllers.uml

import javax.inject.Inject

import controllers.excontrollers.AExerciseAdminController
import model.exercisereading.{JsonReader, ReadingError, ReadingFailure, ReadingResult}
import model.{UmlExTextParser, UmlExercise, UmlExerciseReader}
import play.api.mvc.ControllerComponents
import play.libs.Json

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

class UmlAdmin @Inject()(cc: ControllerComponents)
  extends AExerciseAdminController[UmlExercise](cc, UmlToolObject, UmlExercise.finder, UmlExerciseReader) {

  def checkSolution = Action { implicit request => {
    val solNode = Json.parse("" /* factory.form().bindFromRequest().get(StringConsts.SOLUTION_NAME)*/)
    JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      case Success(_) => Ok("Ok...")
      case Failure(_) => BadRequest("FEHLER!")
    }
  }
  }

  def newExerciseStep2 = Action { implicit request =>
//    exerciseReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
//      case ReadingError(_, _, _) => BadRequest("There has been an error...")
//      case ReadingFailure(_) => BadRequest("There has been an error...")
//      case ReadingResult(exercises) =>
//        val exercise = exercises.head.read.asInstanceOf[UmlExercise]
//        val parser = new UmlExTextParser(exercise.text, exercise.mappings.asScala.toMap, exercise.ignoreWords.asScala.toList)
//        Ok(views.html.umlAdmin.newExerciseStep2Form.render(getUser, exercise, parser.capitalizedWords.toList))
//    }
    Ok("TODO!")
  }

  def newExerciseStep3 = Action { implicit request =>
//    exerciseReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
    //      case ReadingError(_, _, _) => BadRequest("There has been an error...")
    //      case ReadingFailure(_) => BadRequest("There has been an error...")
    //      case ReadingResult(exercises) =>
    //        val exercise = exercises.head.read.asInstanceOf[UmlExercise]
    //        Ok(views.html.umlAdmin.newExerciseStep3Form.render(getUser, exercise))
    //    }
    Ok("TODO!")
  }

}
