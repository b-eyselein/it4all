package controllers.randomExes

import javax.inject.{Inject, Singleton}

import controllers.Secured
import model.JsonFormat
import model.core.Repository
import model.essentials.EssentialsConsts._
import model.essentials.NAryNumber.{parseNaryNumber, parseTwoComplement}
import model.essentials._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

@Singleton
class NaryController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with JsonFormat {

  // Nary Addition

  def newNaryAdditionQuestion(requestedBaseStr: String): EssentialAction = withUser { user =>
    implicit request =>
      val base = if (requestedBaseStr == RandomName) NumberBase.values()(generator.nextInt(3)) // No decimal system...
      else numbaseFromString(requestedBaseStr) getOrElse NumberBase.BINARY

      val sum = generator.nextInt(255) + 1
      val firstSummand = generator.nextInt(sum)

      Ok(views.html.essentials.nAryAdditionQuestion.render(user, new NAryNumber(firstSummand, base), new NAryNumber(sum - firstSummand, base), base, requestedBaseStr))
  }

  def checkNaryAdditionSolution: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asJson flatMap readAddSolutionFromJson match {
        case None           => BadRequest("TODO!")
        case Some(solution) => Ok(Json.obj("correct" -> solution.solutionCorrect))
      }
  }

  private def readAddSolutionFromJson(jsValue: JsValue): Option[NAryAddResult] = jsValue.asObj flatMap { jsObj =>

    jsObj.stringField(BaseName) flatMap numbaseFromString flatMap { base =>

      val maybeSummand1 = jsObj.stringField(FirstSummand) flatMap (parseNaryNumber(_, base))
      val maybeSummand2 = jsObj.stringField(SecondSummand) flatMap (parseNaryNumber(_, base))
      val maybeSolutionNary = jsObj.stringField(LearnerSol) flatMap (parseNaryNumber(_, base))

      (maybeSummand1 zip maybeSummand2 zip maybeSolutionNary).headOption map {
        case ((summand1, summand2), solutionNary) => NAryAddResult(base, summand1, summand2, solutionNary)
      }
    }
  }

  // Nary Conversion

  def newNaryConversionQuestion(fromBaseStr: String, toBaseStr: String): EssentialAction = withUser { user =>
    implicit request =>
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
      Ok(views.html.essentials.nAryConversionQuestion.render(user, new NAryNumber(generator.nextInt(256), fromBase), toBase, fromBaseStr, toBaseStr))
  }

  private def randNumberBase(notBaseOrdinal: Int): NumberBase = {
    var res = generator.nextInt(4)
    while (notBaseOrdinal == res)
      res = generator.nextInt(4)
    NumberBase.values()(res)
  }

  def checkNaryConversionSolution: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asJson flatMap readConvSolutionFromJson match {
        case None           => BadRequest("TODO!")
        case Some(solution) => Ok(Json.obj("correct" -> solution.solutionCorrect))
      }
  }

  private def readConvSolutionFromJson(jsValue: JsValue): Option[NAryConvResult] = jsValue.asObj flatMap { jsObj =>
    val maybeStartingNumBase = jsObj.stringField(StartingNumBase) flatMap numbaseFromString
    val maybeTargetNumBase = jsObj.stringField(TargetNumBase) flatMap numbaseFromString

    (maybeStartingNumBase zip maybeTargetNumBase).headOption flatMap {
      case (startingNumBase, targetNumBase) =>
        val maybeStartingValue = jsObj.stringField(VALUE_NAME) flatMap (parseNaryNumber(_, startingNumBase))
        val maybeLearnerSol = jsObj.stringField(LearnerSol) flatMap (parseNaryNumber(_, targetNumBase))

        (maybeStartingValue zip maybeLearnerSol).headOption map {
          case (startingValue, learnerSol) => NAryConvResult(startingValue, startingNumBase, targetNumBase, learnerSol)
        }
    }
  }

  // Nary Two Complement

  def newNaryTwoComplementQuestion(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.twoComplementQuestion.render(user, NAryNumber(-generator.nextInt(129), NumberBase.DECIMAL), verbose))
  }

  def checkNaryTwoComplementSolutionLive: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asJson flatMap readTwoCompSolutionFromJson match {
        case None           => BadRequest("TODO!")
        case Some(solution) => Ok(Json.obj(
          "correct" -> solution.solutionCorrect,
          "verbose" -> solution.verbose,
          BinaryAbs -> solution.binaryAbsCorrect,
          InvertedAbs -> solution.invertedAbsCorrect))
      }
  }

  private def readTwoCompSolutionFromJson(jsValue: JsValue): Option[TwoCompResult] = jsValue.asObj flatMap { jsObj =>
    val maybeValue = jsObj.intField(VALUE_NAME)
    val maybeSolution = jsObj.stringField(LearnerSol) flatMap parseTwoComplement

    val invertedAbs = jsObj.stringField(InvertedAbs)
    val binaryAbs = jsObj.stringField(BinaryAbs)

    (maybeValue zip maybeSolution).headOption map {
      case (value, solution) => TwoCompResult(value, solution, binaryAbs, invertedAbs)
    }
  }

  private def numbaseFromString(str: String): Option[NumberBase] = Try(Some(NumberBase.valueOf(str))) getOrElse None

}