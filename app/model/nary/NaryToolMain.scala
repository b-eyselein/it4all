package model.nary

import model.Enums.ToolState
import model.core.EvaluationResult
import model.nary.NAryNumber.{parseNaryNumber, parseTwoComplement}
import model.nary.NaryConsts._
import model.toolMains.RandomExerciseToolMain
import model.{Consts, JsonFormat, User}
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.language.implicitConversions
import scala.util.Try

object NaryToolMain extends RandomExerciseToolMain("nary") with JsonFormat {

  // Abstract types

  override type PartType = NaryExPart

  override type R = EvaluationResult

  // Other members

  override val toolname: String = "Zahlensysteme"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = NaryConsts

  override val exParts: Seq[NaryExPart] = NaryExParts.values

  // Views

  override def newExercise(user: User, exPart: NaryExPart, options: Map[String, Seq[String]]): Html = {
    exPart match {
      case NaryAdditionExPart =>

        val requestedBaseStr: String = options.getOrElse("base", Seq("RANDOM")).mkString
        val base = numbaseFromString(requestedBaseStr) getOrElse NumberBase.values()(generator.nextInt(3))

        val sum = generator.nextInt(255) + 1
        val firstSummand = generator.nextInt(sum)

        views.html.nary.nAryAdditionQuestion(user, new NAryNumber(firstSummand, base), new NAryNumber(sum - firstSummand, base), base, requestedBaseStr)

      case NaryConversionExPart =>

        val fromBaseStr = options.getOrElse("fromBase", Seq("RANDOM")).mkString
        val toBaseStr = options.getOrElse("toBase", Seq("RANDOM")).mkString

        val (fromBase, toBase): (NumberBase, NumberBase) = fromBaseStr match {
          case RandomName  => toBaseStr match {
            case RandomName =>
              val fromBase = randNumberBase(-1)
              (fromBase, randNumberBase(fromBase.ordinal))
            case _          =>
              val toBase = numbaseFromString(toBaseStr) getOrElse NumberBase.BINARY
              (randNumberBase(toBase.ordinal), toBase)
          }
          case fromBaseReq =>
            val fromBase = numbaseFromString(fromBaseReq) getOrElse NumberBase.BINARY
            val toBase = toBaseStr match {
              case RandomName => randNumberBase(fromBase.ordinal)
              case _          => numbaseFromString(toBaseStr) getOrElse NumberBase.BINARY
            }
            (fromBase, toBase)
        }

        views.html.nary.nAryConversionQuestion(user, new NAryNumber(generator.nextInt(256), fromBase), toBase, fromBaseStr, toBaseStr)


      case TwoComplementExPart =>
        val verbose = options.getOrElse("verbose", Seq("false")).mkString == "true"
        views.html.nary.twoComplementQuestion(user, NAryNumber(-generator.nextInt(129), NumberBase.DECIMAL), verbose)

    }
  }

  override def index(user: User): Html = views.html.nary.naryOverview(user)

  // Helper functions

  private def randNumberBase(notBaseOrdinal: Int): NumberBase = {
    var res = generator.nextInt(4)
    while (notBaseOrdinal == res)
      res = generator.nextInt(4)
    NumberBase.values()(res)
  }

  // Correction

  override def checkSolution(user: User, exPart: NaryExPart, request: Request[AnyContent]): JsValue = {
    val correctionFunction: JsValue => Option[NAryResult] = exPart match {
      case NaryAdditionExPart   => readAddSolutionFromJson
      case NaryConversionExPart => readConvSolutionFromJson
      case TwoComplementExPart  => readTwoCompSolutionFromJson
    }

    request.body.asJson flatMap correctionFunction match {
      case None           => Json.obj(ERROR -> "TODO!")
      case Some(solution) => solution.toJson
    }
  }

  private def readAddSolutionFromJson(jsValue: JsValue): Option[NAryAddResult] = jsValue.asObj flatMap { jsObj =>
    for {
      base <- jsObj.stringField(BaseName) flatMap numbaseFromString
      summand1 <- jsObj.stringField(FirstSummand) flatMap (parseNaryNumber(_, base))
      summand2 <- jsObj.stringField(SecondSummand) flatMap (parseNaryNumber(_, base))
      solutionNary <- jsObj.stringField(LearnerSol) flatMap (parseNaryNumber(_, base))
    } yield NAryAddResult(base, summand1, summand2, solutionNary)
  }

  private def readConvSolutionFromJson(jsValue: JsValue): Option[NAryConvResult] = jsValue.asObj flatMap { jsObj =>
    for {
      startingNumBase <- jsObj.stringField(StartingNumBase) flatMap numbaseFromString
      targetNumBase <- jsObj.stringField(TargetNumBase) flatMap numbaseFromString
      startingValue <- jsObj.stringField(VALUE_NAME) flatMap (parseNaryNumber(_, startingNumBase))
      learnerSol <- jsObj.stringField(LearnerSol) flatMap (parseNaryNumber(_, targetNumBase))
    } yield NAryConvResult(startingValue, startingNumBase, targetNumBase, learnerSol)
  }

  private def readTwoCompSolutionFromJson(jsValue: JsValue): Option[TwoCompResult] = jsValue.asObj flatMap { jsObj =>
    for {
      value <- jsObj.intField(VALUE_NAME)
      solution <- jsObj.stringField(LearnerSol) flatMap parseTwoComplement
    } yield TwoCompResult(value, solution, jsObj.stringField(BinaryAbs), jsObj.stringField(InvertedAbs))
  }

  private def numbaseFromString(str: String): Option[NumberBase] = Try(Some(NumberBase.valueOf(str))) getOrElse None

}