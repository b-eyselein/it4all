package controllers.exes

import javax.inject.{Inject, Singleton}

import controllers.core.ARandomExController
import model.User
import model.core.{Repository, Secured}
import model.nary.NAryResult._
import model.nary._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

@Singleton
class NaryController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends ARandomExController(cc, dbcp, r, NAryToolObject) with Secured {

  def checkNaryAdditionSolution: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.nary.nAryAdditionResult.render(user, addResultFromFormValue(additionSolution.bindFromRequest.get)))
  }

  def checkNaryConversionSolution: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.nary.nAryConversionResult.render(user, convResultFromFormValue(conversionSolution.bindFromRequest.get)))
  }

  def checkTwoComplementSolution(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.nary.twoComplementResult.render(user, twoCompResultFromFormValue(twoComplementSolution.bindFromRequest.get), verbose))
  }

  def newNaryAdditionQuestion: EssentialAction = withUser { user =>
    implicit request =>
      // No decimal system...
      val base = NumberBase.values()(generator.nextInt(3))

      val sum = generator.nextInt(255) + 1
      val firstSummand = generator.nextInt(sum)

      Ok(views.html.nary.nAryAdditionQuestion.render(user, new NAryNumber(firstSummand, base), new NAryNumber(sum - firstSummand, base), base))
  }

  def newNaryConversionQuestion: EssentialAction = withUser { user =>
    implicit request =>
      val from = generator.nextInt(4)
      var to = generator.nextInt(4)
      while (to == from)
        to = generator.nextInt(4)

      Ok(views.html.nary.nAryConversionQuestion.render(user, new NAryNumber(generator.nextInt(256), NumberBase.values()(from)), NumberBase.values()(to)))
  }

  def newTwoComplementQuestion(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request =>
      // Max negative number in two complement: -128
      Ok(views.html.nary.twoComplementQuestion.render(user, NAryNumber(-generator.nextInt(129), NumberBase.DECIMAL), verbose))
  }

  def renderIndex(user: User): Html = views.html.nary.overview.render(user)
}