package model;

import scala.collection.JavaConverters.{mapAsJavaMapConverter, seqAsJavaListConverter}

import com.fasterxml.jackson.databind.JsonNode

import model.StringConsts.{IGNORE_WORDS_NAME, MAPPINGS_NAME, SOLUTION_NAME}
import model.exercisereading.{ExerciseReader, JsonReader}
import play.data.DynamicForm

object UmlExerciseReader extends ExerciseReader[UmlExercise]("uml", UmlExercise.finder, classOf[Array[UmlExercise]]) {

  override def initRemainingExFromForm(exercise: UmlExercise, form: DynamicForm) {
    exercise.classSelText = form.get("classSelText");
    exercise.diagDrawText = form.get("diagDrawText");
    exercise.solution = form.get("solution");
  }

  override def instantiateExercise(id: Int) = new UmlExercise(id)

  override def updateExercise(exercise: UmlExercise, node: JsonNode) {
    exercise.mappings = JsonReader.readMap(node.get(MAPPINGS_NAME)).asJava;
    exercise.ignoreWords = JsonReader.readTextArray(node.get(IGNORE_WORDS_NAME)).asJava;

    val parser = new UmlExTextParser(exercise.text, exercise.mappings, exercise.ignoreWords);
    exercise.classSelText = parser.parseTextForClassSel;
    exercise.diagDrawText = parser.parseTextForDiagDrawing;

    // Save solution as json in db
    exercise.solution = node.get(SOLUTION_NAME).toString;
  }

}
