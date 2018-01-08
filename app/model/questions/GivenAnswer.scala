package model.questions

trait IdAnswer {
  val id: Int
}

sealed trait GivenAnswer

case class IdGivenAnswer(id: Int) extends GivenAnswer with IdAnswer

case class TextAnswer(text: String) extends GivenAnswer