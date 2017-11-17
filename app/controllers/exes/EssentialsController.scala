package controllers.exes

import javax.inject.{Inject, Singleton}

import controllers.Secured
import controllers.core.ARandomExController
import model.Enums.SuccessType.{NONE, PARTIALLY}
import model.User
import model.core.Repository
import model.essentials.EssentialsConsts._
import model.essentials.NAryResult._
import model.essentials._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class EssentialsController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends ARandomExController(cc, dbcp, r, EssentialsToolObject) with Secured {

  def renderIndex(user: User): Html = views.html.essentials.overview.render(user)

  // Nary

  def checkNaryAdditionSolution(baseStr: String): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.nAryAdditionResult.render(user, addResultFromFormValue(additionSolution.bindFromRequest.get), baseStr))
  }

  def checkNaryConversionSolution(fromBase: String, toBase: String): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.nAryConversionResult.render(user, convResultFromFormValue(conversionSolution.bindFromRequest.get), fromBase, toBase))
  }

  def checkNaryTwoComplementSolution(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.twoComplementResult.render(user, twoCompResultFromFormValue(twoComplementSolution.bindFromRequest.get), verbose))
  }

  def newNaryAdditionQuestion(baseStr: String): EssentialAction = withUser { user =>
    implicit request =>
      val base = if (baseStr == RAND_NAME)
        NumberBase.values()(generator.nextInt(3)) // No decimal system...
      else nbFromString(baseStr)

      val sum = generator.nextInt(255) + 1
      val firstSummand = generator.nextInt(sum)

      Ok(views.html.essentials.nAryAdditionQuestion.render(user, new NAryNumber(firstSummand, base), new NAryNumber(sum - firstSummand, base), base))
  }

  def newNaryConversionQuestion(fromBaseStr: String, toBaseStr: String): EssentialAction = withUser { user =>
    implicit request =>
      val (fromBase, toBase): (NumberBase, NumberBase) = fromBaseStr match {
        case RAND_NAME   => toBaseStr match {
          case RAND_NAME =>
            val fromBase = randNumberBase(None)
            (fromBase, randNumberBase(Some(fromBase)))
          case toBaseReq =>
            val toBase = nbFromString(toBaseStr)
            (randNumberBase(Some(toBase)), toBase)
        }
        case fromBaseReq =>
          val fromBase = nbFromString(fromBaseReq)
          val toBase = toBaseStr match {
            case RAND_NAME => randNumberBase(Some(fromBase))
            case toBaseReq => nbFromString(toBaseStr)
          }
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

  private def nbFromString(str: String) = Try(NumberBase.valueOf(str)).getOrElse(NumberBase.BINARY)

  def newNaryTwoComplementQuestion(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.twoComplementQuestion.render(user, NAryNumber(-generator.nextInt(129), NumberBase.DECIMAL), verbose))
  }

  // Boolean Algebra

  def checkBoolFilloutSolution: EssentialAction = withUser { user =>
    implicit request =>
      request.body.asFormUrlEncoded match {
        case None       => BadRequest("There has been an error!")
        case Some(data) =>
          BoolNodeParser.parse(data(FORMULA_NAME).mkString("")) match {
            case Some(formula) =>

              val assignments = BoolAssignment.generateAllAssignments(formula.usedVariables.toList).map(assignment =>
                assignment + (LEA_VAR -> (ONE == data(assignment.toString).mkString(""))) + (SOL_VAR -> formula.evaluate(assignment)))

              val question = new FilloutQuestion(formula, assignments)

              Ok(views.html.essentials.boolfilloutsolution.render(user, question))

            case None => BadRequest("There has been an error!")
          }
      }
  }

  def newBoolCreationQuestion: EssentialAction = withUser { user => implicit request => Ok(views.html.essentials.boolcreatequestion.render(user, CreationQuestion.generateNew)) }

  def newBoolFilloutQuestion: EssentialAction = withUser { user => implicit request => Ok(views.html.essentials.boolfilloutquestion.render(user, FilloutQuestion.generateNew)) }

  def checkBoolCreationSolution: EssentialAction = withUser { user =>
    implicit request =>
      request.body.asFormUrlEncoded match {
        case None       => BadRequest("There has been an error!")
        case Some(data) => Ok(views.html.essentials.boolcreatesolution.render(user, checkCreationSolution(data)))
      }
  }

  def checkBoolCreationSolutionLive: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asFormUrlEncoded match {
        case None => BadRequest("There has been an error!")

        case Some(data) =>
          var result = checkCreationSolution(data)
          // FIXME: ugly, stupid, sh**ty hack!
          Ok(play.api.libs.json.Json.parse(play.libs.Json.toJson(result).toString))
      }
  }

  private def checkCreationSolution(data: Map[String, Seq[String]]): BooleanQuestionResult = {
    val learnerSolution = data(FORM_VALUE).mkString
    BoolNodeParser.parse(learnerSolution) match {
      case None =>
        new BooleanQuestionResult(NONE, learnerSolution, null)
      //        throw new CorrectionException(learnerSolution, "Formula could not be parsed!")
      case Some(formula) =>
        val variables = data(VARS_NAME).mkString.split(",").map(variab => Variable(variab.charAt(0))).toList

        // Check that formula only contains variables found in form
        val wrongVars = formula.usedVariables.filter(!variables.contains(_))
        //        if (wrongVars.nonEmpty)
        //          throw new CorrectionException(learnerSolution, s"In ihrer Loesung wurde(n) die folgende(n) falsche(n) Variable(n) benutzt: '${wrongVars.mkString(", ")}'")

        val assignments = BoolAssignment
          .generateAllAssignments(variables)
          .map(as => as + (LEA_VAR -> formula.evaluate(as)) + (SOL_VAR -> (data(as.toString()).mkString == ONE)))

        val question = new CreationQuestion(variables, assignments)
        new BooleanQuestionResult(PARTIALLY, learnerSolution, question)
    }
  }

}