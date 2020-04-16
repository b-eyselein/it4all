package model.persistence

import model.tools.ExPart
import play.api.libs.json.JsValue

final case class DbExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  difficulty: Int,
  sampleSolutionsJson: JsValue,
  contentJson: JsValue
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
