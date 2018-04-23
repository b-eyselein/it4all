package model.learningPath

import play.api.libs.json.JsValue


trait LearningPathSection {

  val id    : Int
  val pathId: Int
  val title : String

}

case class TextSection(id: Int, pathId: Int, title: String, text: String) extends LearningPathSection


case class LPQuestion(questionText: String, solution: JsValue)

case class QuestionSection(id: Int, pathId: Int, title: String, questions: Seq[LPQuestion]) extends LearningPathSection


case class LearningPath(id: Int, title: String, sections: Seq[LearningPathSection])
