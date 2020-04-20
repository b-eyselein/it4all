package model.persistence

import model.tools.{ExPart, Exercise, Topic}
import play.api.libs.json.JsValue

object ADbModels {

  def dbExercisetoExercise(dbExercise: DbExercise, topics: Seq[Topic]): Exercise = dbExercise match {
    case DbExercise(id, collectionId, toolId, title, authors, text, difficulty) =>
      Exercise(id, collectionId, toolId, title, authors, text, topics, difficulty)
  }

  def exerciseToDbExercise(exercise: Exercise): (DbExercise, Seq[DbExerciseTopic]) = exercise match {
    case Exercise(id, collectionId, toolId, title, authors, text, topics, difficulty) =>
      val dbEx     = DbExercise(id, collectionId, toolId, title, authors, text, difficulty)
      val dbTopics = topics.map(t => DbExerciseTopic(t.id, id, collectionId, toolId))

      (dbEx, dbTopics)
  }

}

final case class DbExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  difficulty: Int
)

final case class DbExerciseTopic(
  topicId: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String
)

final case class DbUserSolution(
  id: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  part: ExPart,
  username: String,
  solution: JsValue
)

final case class DbLesson(
  id: Int,
  toolId: String,
  title: String,
  description: String,
  contentJson: JsValue
)
