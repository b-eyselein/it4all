package model.nary

import play.api.libs.json._
import model.nary.NaryConsts._
import play.api.libs.functional.syntax._

//noinspection ConvertibleToMethodValue
object NarySolutionJsonFormat {

  private implicit val numberBaseReads: Reads[NumberBase] = {
    // FIXME: Test!
    case JsString(str) => JsSuccess(NumberBase.withNameInsensitive(str))
    case _             => JsError()
  }

  private implicit val naryNumberReads: Reads[NAryNumber] = {
    // FIXME: test!
    case JsString(str) => NAryNumber.parseNaryNumber("", NumberBase.BINARY)
  }

  val naryAddResultJsonReads: Reads[NAryAddResult] = (
    (__ \ BaseName).read[NumberBase] and
      (__ \ FirstSummand).read[NAryNumber] and
      (__ \ SecondSummand).read[NAryNumber] and
      (__ \ solutionName).read[NAryNumber]
    ) (NAryAddResult.apply(_, _, _, _))

}
