package model;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import play.api.data.Mapping;
import play.data.DynamicForm;
import model.StringConsts._
import scala.collection.JavaConverters._
import model.exercisereading.JsonReader
import play.Logger

object UmlExerciseReader extends ExerciseReader[UmlExercise]("uml", UmlExercise.finder, classOf[Array[UmlExercise]]) {

  override def initRemainingExFromForm(exercise: UmlExercise, form: DynamicForm) {
    exercise.classSelText = form.get("classSelText");
    exercise.diagDrawText = form.get("diagDrawText");
    exercise.solution = form.get("solution");
  }

  override def instantiateExercise(id: Int) = new UmlExercise(id)

  override def updateExercise(exercise: UmlExercise, node: JsonNode) {
    val text = "";

    exercise.mappings = JsonReader.readMap(node.get(MAPPINGS_NAME)).asJava;
    exercise.ignoreWords = JsonReader.readTextArray(node.get(IGNORE_WORDS_NAME)).asJava;

    val parser = new UmlExTextParser(exercise.text, exercise.mappings, exercise.ignoreWords);
    exercise.classSelText = parser.parseTextForClassSel;
    exercise.diagDrawText = parser.parseTextForDiagDrawing;

    // Save solution as json in db
    exercise.solution = node.get(SOLUTION_NAME).toString;
    
    Logger.debug(exercise.solution);
  }

}
