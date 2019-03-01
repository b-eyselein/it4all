package model.nary

import javax.inject.{Inject, Singleton}
import model.User
import model.core.result.EvaluationResult
import model.nary.NaryConsts._
import model.toolMains.{RandomExerciseToolMain, ToolState}
import play.api.Logger
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

@Singleton
class NaryToolMain @Inject()(val tables: NaryTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain("Zahlensysteme", "nary") {

  private val logger = Logger(classOf[NaryToolMain])

  // Abstract types

  override type PartType = NaryExPart

  override type ResultType = EvaluationResult

  override type Tables = NaryTableDefs

  // Other members

  override val toolState: ToolState = ToolState.LIVE

  override protected val exParts: Seq[NaryExPart] = NaryExParts.values

  // Views

  override def exercisesOverviewForIndex: Html =
    views.html.toolViews.nary.naryOverview(this)

  override def newExercise(user: User, exPart: NaryExPart, options: Map[String, Seq[String]])
                          (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = exPart match {
    case NaryExParts.NaryAdditionExPart =>

      val requestedBaseStr: String = options.getOrElse("base", Seq(RandomName)).mkString
      val base = numbaseFromString(requestedBaseStr) getOrElse NumberBase.values(generator.nextInt(3))

      val sum = generator.nextInt(255) + 1
      val firstSummand = generator.nextInt(sum)

      views.html.toolViews.nary.nAryAdditionQuestion(user, new NAryNumber(firstSummand, base), new NAryNumber(sum - firstSummand, base), base, requestedBaseStr, this)

    case NaryExParts.NaryConversionExPart =>

      val fromBaseStr: String = options.get("fromBase").map(_.mkString).getOrElse(RandomName)
      val toBaseStr: String = options.get("toBase").map(_.mkString).getOrElse(RandomName)

      val (fromBase, toBase): (NumberBase, NumberBase) = fromBaseStr match {
        case RandomName  => toBaseStr match {
          case RandomName =>
            val (fromBase, remaining) = randAndRemainingNumberBase(NumberBase.values)
            (fromBase, randAndRemainingNumberBase(remaining)._1)
          case _          =>
            val toBase = numbaseFromString(toBaseStr) getOrElse NumberBase.Binary
            (randAndRemainingNumberBase(NumberBase.values.filter(_ != toBase))._1, toBase)
        }
        case fromBaseReq =>
          val fromBase = numbaseFromString(fromBaseReq) getOrElse NumberBase.Binary
          val toBase = toBaseStr match {
            case RandomName => randAndRemainingNumberBase(NumberBase.values.filter(_ != fromBase))._1
            case _          => numbaseFromString(toBaseStr) getOrElse NumberBase.Binary
          }
          (fromBase, toBase)
      }

      views.html.toolViews.nary.nAryConversionQuestion(user, new NAryNumber(generator.nextInt(256), fromBase), toBase, fromBaseStr, toBaseStr, this)


    case NaryExParts.TwoComplementExPart =>
      val verbose = options.getOrElse("verbose", Seq("false")).mkString == "true"
      views.html.toolViews.nary.twoComplementQuestion(user, NAryNumber(-generator.nextInt(129), NumberBase.Decimal), verbose, this)

  }

  // Helper functions

  private def randAndRemainingNumberBase(remainingBases: Seq[NumberBase]): (NumberBase, Seq[NumberBase]) = {
    val shuffledBases = generator.shuffle(remainingBases)
    (shuffledBases.head, shuffledBases.tail)
  }

  // Correction

  override def checkSolution(exPart: NaryExPart, request: Request[AnyContent]): JsValue = request.body.asJson match {
    case None          =>
      logger.error("A solution for an nary exercise needs to be sent in json format!")
      ???
    case Some(jsValue) =>
      NarySolutionJsonFormat.readSolutionFromJson(exPart, jsValue) match {
        case JsError(jsErrors)                               =>
          jsErrors.foreach(println)
          Json.obj(errorName -> "TODO!")
        case JsSuccess(maybeSolution: Option[NAryResult], _) => maybeSolution match {
          case None           => ???
          case Some(solution) => solution.toJson
        }
      }
  }

  // Reading functions

  private def numbaseFromString(str: String): Option[NumberBase] = NumberBase.withNameInsensitiveOption(str)

}
