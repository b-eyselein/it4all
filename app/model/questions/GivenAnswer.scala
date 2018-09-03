package model.questions

trait IdAnswer {

  val id: Int

}

sealed trait GivenAnswer

final case class IdGivenAnswer(id: Int) extends GivenAnswer with IdAnswer

final case class TextAnswer(text: String) extends GivenAnswer