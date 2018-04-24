package model.learningPath

import model.core.CoreConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object LPQuestionJsonFormat {

  private val lpQuestionReads: Reads[LPQuestion] = ((__ \ textName).read[String] and (__ \ solutionName).read[JsValue]) (LPQuestion.apply(_, _))

  private val lpQuestionWrites: Writes[LPQuestion] = ((__ \ textName).write[String] and (__ \ solutionName).write[JsValue]) (unlift(LPQuestion.unapply))

  val lpQuestionJsonFormat = Format(lpQuestionReads, lpQuestionWrites)

}
