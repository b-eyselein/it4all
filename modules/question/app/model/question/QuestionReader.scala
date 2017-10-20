package model.question

import com.fasterxml.jackson.databind.JsonNode
import model.StringConsts._
import model.exercisereading.{ExerciseCollectionReader, ExerciseReader, JsonReader}
import model.quiz.Quiz
import play.libs.Json

import scala.collection.JavaConverters._

object QuestionReader extends ExerciseReader[Question]("question", Question.finder, classOf[Array[Question]]) {

  private def readAnswer(answerNode: JsonNode): Answer = {
    val key = Json.fromJson(answerNode.get(KEY_NAME), classOf[AnswerKey])

    val answer = Option(Answer.finder.byId(key)).getOrElse(new Answer(key))

    answer.correctness = Correctness.valueOf(answerNode.get("correctness").asText())
    answer.text = JsonReader.readAndJoinTextArray(answerNode.get(TEXT_NAME))
    answer
  }

  //  override def initRemainingExFromForm(exercise: Question, form: DynamicForm): Unit = {
  //    exercise.maxPoints = Integer.parseInt(form.get(MAX_POINTS))
  //    exercise.questionType = Question.QType.valueOf(form.get(EXERCISE_TYPE))
  //  }

  override def instantiate(id: Int): Question = new Question(id)

  override def save(exercise: Question): Unit = {
    exercise.save()
    exercise.answers.forEach(_.save())
  }

  override def updateExercise(exercise: Question, exerciseNode: JsonNode): Unit = {
    exercise.maxPoints = exerciseNode.get(MAX_POINTS).asInt()
    exercise.questionType = Question.QType.valueOf(exerciseNode.get(EXERCISE_TYPE).asText())

    exercise.answers = ExerciseReader.readArray(exerciseNode.get(ANSWERS_NAME), readAnswer).asJava
  }

}

object QuizReader extends ExerciseCollectionReader[Question, Quiz]("question", Quiz.finder, classOf[Array[Quiz]]) {

  //  def initRemainingExFromForm(exercise: Quiz, form: DynamicForm): Unit = exercise.setTheme(form.get("theme"))

  override def instantiate(id: Int) = new Quiz(id)

  override def updateCollection(coll: Quiz, node: JsonNode): Unit = {}

  protected def updateExercise(exercise: Quiz, exerciseNode: JsonNode): Unit = exercise.setTheme(exerciseNode.get("theme").asText())

}
