package model.learningPath

import play.api.libs.json.JsValue

trait LearningPathSection {

  val id: Int
  val toolUrl: String
  val pathId: Int
  val title: String

}

final case class TextSection(id: Int, toolUrl: String, pathId: Int, title: String, text: String)
    extends LearningPathSection

final case class LPQuestion(questionText: String, solution: JsValue)

final case class QuestionSection(id: Int, toolUrl: String, pathId: Int, title: String, questions: Seq[LPQuestion])
    extends LearningPathSection

final case class LearningPath(toolUrl: String, id: Int, title: String, sections: Seq[LearningPathSection])
