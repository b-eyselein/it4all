package controllers.randomExes

import javax.inject.{Inject, Singleton}

import controllers.Secured
import model.Enums.SuccessType.PARTIALLY
import model.core.Repository
import model.essentials.BoolNodeParser.parseBoolFormula
import model.essentials.BooleanQuestion._
import model.essentials.EssentialsConsts._
import model.essentials.NAryResult._
import model.essentials._
import model.{JsonFormat, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.libs.{Json => JavaJson}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

@Singleton
class EssentialsController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends ARandomExController(cc, dbcp, r, EssentialsToolObject) with Secured with JsonFormat {

  def renderIndex(user: User): Html = views.html.essentials.overview.render(user)

  // Nary

  // FIXME: nary komplett auf AJAX!

  def checkNaryAdditionSolution(baseStr: String): EssentialAction = withUser { user =>
    implicit request =>
      additionSolution.bindFromRequest.fold(_ => BadRequest("TODO!"),
        solution => Ok(views.html.essentials.nAryAdditionResult.render(user, addResultFromFormValue(solution), baseStr)))
  }

  def checkNaryConversionSolution(fromBase: String, toBase: String): EssentialAction = withUser { user =>
    implicit request =>
      conversionSolution.bindFromRequest.fold(_ => BadRequest("TODO!"),
        solution => Ok(views.html.essentials.nAryConversionResult.render(user, convResultFromFormValue(solution), fromBase, toBase)))
  }

  def checkNaryTwoComplementSolution(verbose: Boolean): EssentialAction = withUser { user =>
    implicit request =>
      twoComplementSolution.bindFromRequest.fold(_ => BadRequest("TODO!"),
        solution => Ok(views.html.essentials.twoComplementResult.render(user, twoCompResultFromFormValue(solution), verbose)))
  }

  def newNaryAdditionQuestion(baseStr: String): EssentialAction = withUser { user =>
    implicit request =>
      val base = if (baseStr == RandomName) NumberBase.values()(generator.nextInt(3)) // No decimal system...
      else nbFromString(baseStr)

      val sum = generator.nextInt(255) + 1
      val firstSummand = generator.nextInt(sum)

      Ok(views.html.essentials.nAryAdditionQuestion.render(user, new NAryNumber(firstSummand, base), new NAryNumber(sum - firstSummand, base), base))
  }

  def newNaryConversionQuestion(fromBaseStr: String, toBaseStr: String): EssentialAction = withUser { user =>
    implicit request =>
      val (fromBase, toBase): (NumberBase, NumberBase) = fromBaseStr match {
        case RandomName  => toBaseStr match {
          case RandomName =>
            val fromBase = randNumberBase(None)
            (fromBase, randNumberBase(Some(fromBase)))
          case _          =>
            val toBase = nbFromString(toBaseStr)
            (randNumberBase(Some(toBase)), toBase)
        }
        case fromBaseReq =>
          val fromBase = nbFromString(fromBaseReq)
          val toBase = toBaseStr match {
            case RandomName => randNumberBase(Some(fromBase))
            case _          => nbFromString(toBaseStr)
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

  def newBoolFilloutQuestion(opsAsSymbols: Boolean): EssentialAction = withUser { user =>
    implicit request => Ok(views.html.essentials.boolfilloutquestion.render(user, generateNewFilloutQuestion, opsAsSymbols))
  }

  def checkBoolFilloutSolution: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asJson flatMap readFilloutQuestionResultFromJson match {
        case None                        => BadRequest("There has been an error!")
        case Some(filloutQuestionResult) =>

          val ret = JsArray(filloutQuestionResult.assignments map { assignment =>
            val fields: Seq[(String, JsValue)] = assignment.assignments map { case (variab, bool) => variab.asString -> JsBoolean(bool) } toSeq

            JsObject(fields ++ Option("id" -> JsString(assignment.identifier)))
          })

          Ok(ret)
      }
  }

  def newBoolCreationQuestion: EssentialAction = withUser { user => implicit request => Ok(views.html.essentials.boolcreatequestion.render(user, generateNewCreationQuestion)) }


  def checkBoolCreationSolution: EssentialAction = withUser { user =>
    implicit request =>
      request.body.asFormUrlEncoded flatMap checkCreationSolution match {
        case None         => BadRequest("There has been an error!")
        case Some(result) => Ok(views.html.essentials.boolcreatesolution.render(user, result))
      }
  }

  def checkBoolCreationSolutionLive: EssentialAction = withUser { _ =>
    implicit request =>
      request.body.asFormUrlEncoded match {
        case None => BadRequest("There has been an error!")

        case Some(data) =>
          val result = checkCreationSolution(data)
          // FIXME: ugly, stupid, sh**ty hack!
          Ok(play.api.libs.json.Json.parse(JavaJson.toJson(result.get).toString))
      }
  }

  // Helper mehtods

  private def readFilloutQuestionResultFromJson(json: JsValue): Option[FilloutQuestionResult] = json.asObj flatMap { jsObj =>

    val maybeFormula: Option[ScalaNode] = jsObj.stringField(FormulaName) flatMap parseBoolFormula

    val maybeAssignments: Option[Seq[BoolAssignment]] = jsObj.arrayField(AssignmentsName, _.asObj flatMap readAssignmentsObject)

    (maybeFormula zip maybeAssignments).headOption map {
      case (formula, assignments) => FilloutQuestionResult(formula, assignments map (as => as + (SolVariable -> formula(as))))
    }
  }

  private def readAssignmentsObject(jsObject: JsObject): Option[BoolAssignment] = jsObject.asMap(_.apply(0), _.asBool) map {
    mapping => mapping map (strAndVal => (Variable(strAndVal._1), strAndVal._2))
  } map BoolAssignment.apply

  private def checkCreationSolution(data: Map[String, Seq[String]]): Option[CreationQuestionResult] = {
    // FIXME: get per json, read from json!
    val learnerSolution = data(FORM_VALUE) mkString

    parseBoolFormula(learnerSolution) map { formula =>
      val variables = data(VARS_NAME).mkString split "," map (v => Variable(v(0))) toSet

      // Check that formula only contains variables found in form
      //      val wrongVars = formula.usedVariables filter (!variables.contains(_))
      //        if (wrongVars.nonEmpty)
      //          throw new CorrectionException(learnerSolution, s"In ihrer Loesung wurde(n) die folgende(n) falsche(n) Variable(n) benutzt: '${wrongVars.mkString(", ")}'")

      val assignments = BoolAssignment
        .generateAllAssignments(variables toSeq)
        .map(as => as + (LerVariable -> formula(as)) + (SolVariable -> (data(as.toString).mkString == One)))

      CreationQuestionResult(PARTIALLY, learnerSolution, CreationQuestion(variables, assignments))
    }
  }

}