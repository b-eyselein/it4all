package controllers.exes

import javax.inject.{Inject, Singleton}

import controllers.Secured
import controllers.core.ARandomExController
import model.User
import model.core.Repository
import model.nary.NAryResult._
import model.nary.NaryConsts._
import model.nary._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class EssentialsController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends ARandomExController(cc, dbcp, r, NAryToolObject) with Secured {


  def checkNaryAdditionSolution: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.nAryAdditionResult.render(user, addResultFromFormValue(additionSolution.bindFromRequest.get)))
  }

  def checkNaryConversionSolution: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.nAryConversionResult.render(user, convResultFromFormValue(conversionSolution.bindFromRequest.get)))
  }

  def checkNaryTwoComplementSolution(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.twoComplementResult.render(user, twoCompResultFromFormValue(twoComplementSolution.bindFromRequest.get), verbose))
  }

  def newNaryAdditionQuestion: EssentialAction = withUser { user =>
    implicit request =>
      val base = NumberBase.values()(generator.nextInt(3)) // No decimal system...

      val sum = generator.nextInt(255) + 1
      val firstSummand = generator.nextInt(sum)

      Ok(views.html.essentials.nAryAdditionQuestion.render(user, new NAryNumber(firstSummand, base), new NAryNumber(sum - firstSummand, base), base))
  }

  def newNaryConversionQuestion(fromBaseStr: String, toBaseStr: String): EssentialAction = withUser { user =>
    implicit request =>
      val (fromBase, toBase): (NumberBase, NumberBase) = (fromBaseStr, toBaseStr) match {
        case (RAND_NAME, RAND_NAME)   =>
          val fromBase = randNumberBase(None)
          (fromBase, randNumberBase(Some(fromBase)))
        case (RAND_NAME, toBaseReq)   =>
          val toBase = Try(NumberBase.valueOf(toBaseStr)).getOrElse(NumberBase.BINARY)
          (randNumberBase(Some(toBase)), toBase)
        case (fromBaseReq, RAND_NAME) =>
          val fromBase = Try(NumberBase.valueOf(fromBaseStr)).getOrElse(NumberBase.BINARY)
          (fromBase, randNumberBase(Some(fromBase)))
        case (fromBaseReq, toBaseReq) =>
          val fromBase = Try(NumberBase.valueOf(fromBaseStr)).getOrElse(NumberBase.BINARY)
          val toBase = Try(NumberBase.valueOf(toBaseStr)).getOrElse(NumberBase.BINARY)
          (fromBase, toBase)
      }
      Ok(views.html.essentials.nAryConversionQuestion.render(user, new NAryNumber(generator.nextInt(256), fromBase), toBase))
  }

  private def randNumberBase(notBase: Option[NumberBase]): NumberBase = {
    var res = generator.nextInt(4)
    while (notBase.isDefined && notBase.get.ordinal == res)
      res = generator.nextInt(4)
    NumberBase.values()(res)
  }

  def newNaryTwoComplementQuestion(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.twoComplementQuestion.render(user, NAryNumber(-generator.nextInt(129), NumberBase.DECIMAL), verbose))
  }

  def renderIndex(user: User): Html = views.html.essentials.overview.render(user)
}