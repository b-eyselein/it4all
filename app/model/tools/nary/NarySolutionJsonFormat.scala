package model.tools.nary

import model.tools.nary.NaryConsts._
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object NarySolutionJsonFormat {

  private val logger = Logger(NarySolutionJsonFormat.getClass)

  // FIXME: Test!
  private implicit val numberBaseReads: Reads[NumberBase] = {
    case JsString(str) => NumberBase.withNameInsensitiveOption(str) match {
      case Some(nb) => JsSuccess(nb)
      case None     => JsError(str + " is no legal number base!")
    }
    case _             => JsError("A Numberbase needs to be a string!")
  }

  def readSolutionFromJson(exPart: NaryExPart, solutionJson: JsValue): JsResult[Option[NAryResult]] = {
    logger.warn(Json.prettyPrint(solutionJson))

    exPart match {
      case NaryExParts.NaryAdditionExPart   => naryAddResultJsonReads.reads(solutionJson)
      case NaryExParts.NaryConversionExPart => naryConvResultReads.reads(solutionJson)
      case NaryExParts.TwoComplementExPart  => twoCompResultReads.reads(solutionJson)
    }
  }

  private def applyNaryAddResult(base: NumberBase, firstSummandStr: String, secondSummandStr: String, solutionNumberStr: String): Option[NAryAddResult] = for {
    firstSummand <- NAryNumber.parseNaryNumber(firstSummandStr, base)
    secondSummand <- NAryNumber.parseNaryNumber(secondSummandStr, base)
    solution <- NAryNumber.parseNaryNumber(solutionNumberStr, base)
  } yield NAryAddResult(base, firstSummand, secondSummand, solution)

  private val naryAddResultJsonReads: Reads[Option[NAryAddResult]] = (
    (__ \ BaseName).read[NumberBase] and
      (__ \ FirstSummand).read[String] and
      (__ \ SecondSummand).read[String] and
      (__ \ solutionName).read[String]
    ) (applyNaryAddResult(_, _, _, _))


  private def applyNAryConvResult(valueStr: String, startingNumBase: NumberBase, targetNumBase: NumberBase, solutionNumberStr: String): Option[NAryConvResult] = for {
    value <- NAryNumber.parseNaryNumber(valueStr, startingNumBase)
    solution <- NAryNumber.parseNaryNumber(solutionNumberStr, targetNumBase)
  } yield NAryConvResult(value, startingNumBase, targetNumBase, solution)

  private val naryConvResultReads: Reads[Option[NAryConvResult]] = (
    (__ \ valueName).read[String] and
      (__ \ StartingNumBase).read[NumberBase] and
      (__ \ TargetNumBase).read[NumberBase] and
      (__ \ solutionName).read[String]
    ) (applyNAryConvResult(_, _, _, _))


  private def applyTwoCompResult(value: Int, solutionNumberStr: String, maybeBinaryAbsStr: Option[String], maybeInvertedAbsStr: Option[String]): Option[TwoCompResult] = for {
    solution <- NAryNumber.parseTwoComplement(solutionNumberStr)
  } yield TwoCompResult(value, solution, maybeBinaryAbsStr, maybeInvertedAbsStr)

  private val twoCompResultReads: Reads[Option[TwoCompResult]] = (
    (__ \ valueName).read[Int] and
      (__ \ solutionName).read[String] and
      (__ \ BinaryAbs).readNullable[String] and
      (__ \ InvertedAbs).readNullable[String]
    ) (applyTwoCompResult(_, _, _, _))

}
