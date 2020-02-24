package model.persistence

import model.tools.collectionTools.{ExPart, SemanticVersion}
import play.api.libs.json.JsValue

final case class DbUserSolution(
  id: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  part: ExPart,
  username: String,
  solution: JsValue
)

final case class DbLesson(
  id: Int,
  toolId: String,
  title: String,
  contentJson: JsValue
)
