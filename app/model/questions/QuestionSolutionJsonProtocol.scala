package model.questions

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CommitedQuestionSolution(questionType: QuestionType, chosen: Seq[Int])

//noinspection ConvertibleToMethodValue
object QuestionSolutionJsonProtocol {

  val questionSolutionReads: Reads[CommitedQuestionSolution] = (
    (__ \ "questionType").read[QuestionType](QuestionTypes.jsonFormat) and
      (__ \ "chosen").read[Seq[Int]]
    ) (CommitedQuestionSolution.apply(_, _))

  //  val givenAnswerReads: Reads[GivenAnswer] = ???


}
