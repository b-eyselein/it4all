package controllers.nary

import javax.inject.{Inject, Singleton}

import controllers.core.excontrollers.ARandomExController
import model.User
import model.core.{Repository, Secured}
import model.nary._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Random

object NaryController {
  val generator = new Random
}

@Singleton
class NaryController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                              (implicit ec: ExecutionContext)
  extends ARandomExController(cc, dbcp, r, NAryToolObject)  with Secured {


  def checkNaryAdditionSolution: EssentialAction = withUser { user =>
    implicit request =>
      val result = null //NAryAddResult.parseFromForm(factory.form().bindFromRequest())
      Ok(views.html.nary.nAryAdditionResult.render(user, result))
  }

  def checkNaryConversionSolution: EssentialAction = withUser { user =>
    implicit request =>
      val result = null // NAryConvResult.parseFromForm(factory.form().bindFromRequest())
      Ok(views.html.nary.nAryConversionResult.render(user, result))
  }

  def checkTwoComplementSolution(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request =>
      val result = null // TwoCompResult.parseFromForm(factory.form().bindFromRequest(), verbose)
      Ok(views.html.nary.twoComplementResult.render(user, result, verbose))
  }

  def newNaryAdditionQuestion: EssentialAction = withUser { user =>
    implicit request =>
      val base = NumberBase.takeRandom()

      val sum = NaryController.generator.nextInt(255) + 1
      val firstSummand = NaryController.generator.nextInt(sum)

      val first = new NAryNumber(firstSummand, base)
      val second = new NAryNumber(sum - firstSummand, base)

      Ok(views.html.nary.nAryAdditionQuestion.render(user, first, second, base))
  }

  def newNaryConversionQuestion: EssentialAction = withUser { user =>
    implicit request =>
      val value = NaryController.generator.nextInt(256)

      val from = NaryController.generator.nextInt(4)
      var to = NaryController.generator.nextInt(4)
      while (to == from)
        to = NaryController.generator.nextInt(4)

      val fromNB = NumberBase.takeRandom()
      val toNB = NumberBase.takeRandom()

      val startingNumber = new NAryNumber(value, fromNB)

      Ok(views.html.nary.nAryConversionQuestion.render(user, value, fromNB, toNB, startingNumber))
  }

  def newTwoComplementQuestion(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request =>
      // Max negative number in two complement: -128
      val value = -NaryController.generator.nextInt(129)
      Ok(views.html.nary.twoComplementQuestion.render(user, value, verbose))
  }

  def renderIndex(user: User): Html = views.html.nary.overview.render(user)
}