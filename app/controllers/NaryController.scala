package controllers

import javax.inject.{Inject, Singleton}

import controllers.NaryController._
import controllers.excontrollers.ARandomExController
import model.nary._
import model.user.User
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.util.Random

object NaryController {
  val generator = new Random
}

@Singleton
class NaryController @Inject()(cc: ControllerComponents) extends ARandomExController(cc, NAryToolObject) {


  def checkNaryAdditionSolution = Action { implicit request =>
    val result = null //NAryAddResult.parseFromForm(factory.form().bindFromRequest())
    Ok(views.html.nary.nAryAdditionResult.render(getUser, result))
  }

  def checkNaryConversionSolution = Action { implicit request =>
    val result = null // NAryConvResult.parseFromForm(factory.form().bindFromRequest())
    Ok(views.html.nary.nAryConversionResult.render(getUser, result))
  }

  def checkTwoComplementSolution(verbose: Boolean) = Action { implicit request =>
    val result = null // TwoCompResult.parseFromForm(factory.form().bindFromRequest(), verbose)
    Ok(views.html.nary.twoComplementResult.render(getUser, result, verbose))
  }

  def newNaryAdditionQuestion = Action { implicit request =>
    val base = NumberBase.takeRandom()

    val sum = generator.nextInt(255) + 1
    val firstSummand = generator.nextInt(sum)

    val first = new NAryNumber(firstSummand, base)
    val second = new NAryNumber(sum - firstSummand, base)

    Ok(views.html.nary.nAryAdditionQuestion.render(getUser, first, second, base))
  }

  def newNaryConversionQuestion = Action { implicit request =>
    val value = generator.nextInt(256)

    val from = generator.nextInt(4)
    var to = generator.nextInt(4)
    while (to == from)
      to = generator.nextInt(4)

    val fromNB = NumberBase.takeRandom()
    val toNB = NumberBase.takeRandom()

    val startingNumber = new NAryNumber(value, fromNB)

    Ok(views.html.nary.nAryConversionQuestion.render(getUser, value, fromNB, toNB, startingNumber))
  }

  def newTwoComplementQuestion(verbose: Boolean) = Action { implicit request =>
    // Max negative number in two complement: -128
    val value = -generator.nextInt(129)
    Ok(views.html.nary.twoComplementQuestion.render(getUser, value, verbose))
  }

  def renderIndex(user: User): Html = views.html.nary.overview.render(user)
}