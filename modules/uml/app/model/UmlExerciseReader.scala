package model

import com.fasterxml.jackson.databind.JsonNode
import model.StringConsts.{IGNORE_WORDS_NAME, MAPPINGS_NAME, SOLUTION_NAME}
import model.exercisereading.{ExerciseReader, JsonReader}

import scala.collection.JavaConverters._


object UmlExerciseReader extends ExerciseReader[UmlExercise]("uml", UmlExercise.finder, classOf[Array[UmlExercise]]) {

//  override def initRemainingExFromForm(exercise: UmlExercise, form: DynamicForm) {
  //    exercise.classSelText = form.get("classSelText")
  //    exercise.diagDrawText = form.get("diagDrawText")
  //    exercise.solution = form.get("solution")
  //  }

  override def instantiate(id: Int): UmlExercise = {
    val ex = new UmlExercise()
    ex.id = id
    ex
  }

  override def updateExercise(exercise: UmlExercise, node: JsonNode) {
    val mappings = JsonReader.readMap(node.get(MAPPINGS_NAME))
    val ignoreWords = JsonReader.readTextArray(node.get(IGNORE_WORDS_NAME))

    exercise.mappings = mappings.asJava
    exercise.ignoreWords = ignoreWords.asJava


    val parser = new UmlExTextParser(exercise.text, mappings, ignoreWords)
    exercise.classSelText = parser.parseTextForClassSel
    exercise.diagDrawText = parser.parseTextForDiagDrawing

    // Save solution as json in db
    exercise.solution = node.get(SOLUTION_NAME).toString
  }

}