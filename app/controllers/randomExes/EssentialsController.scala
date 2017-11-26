package controllers.randomExes

import javax.inject.{Inject, Singleton}

import controllers.Secured
import model.Enums.SuccessType.PARTIALLY
import model.User
import model.core.Repository
import model.essentials.EssentialsConsts._
import model.essentials.NAryResult._
import model.essentials._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.libs.{Json => JavaJson}
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.{implicitConversions, postfixOps}
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
      val base = if (baseStr == RandomName)
        NumberBase.values()(generator.nextInt(3)) // No decimal system...
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
          case toBaseReq  =>
            val toBase = nbFromString(toBaseStr)
            (randNumberBase(Some(toBase)), toBase)
        }
        case fromBaseReq =>
          val fromBase = nbFromString(fromBaseReq)
          val toBase = toBaseStr match {
            case RandomName => randNumberBase(Some(fromBase))
            case toBaseReq  => nbFromString(toBaseStr)
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

  def checkBoolFilloutSolutionLive: EssentialAction = withUser { user =>
    implicit request =>
      request.body.asJson match {
        case None       => BadRequest("There has been an error!");
        case Some(json) =>
          println(json)
          readFilloutQuestionFromJson(json) match {
            case None                  => BadRequest("There has been an error!")
            case Some(filloutQuestion) =>
              // FIXME: implement correction!

              println(filloutQuestion)

              Ok("TODO!")
          }
      }
  }

  def checkBoolFilloutSolution: EssentialAction = withUser { user =>
    implicit request =>
      request.body.asFormUrlEncoded match {
        case None       => BadRequest("There has been an error!")
        case Some(data) =>
          println(data map (d => d._1 + " -> " + d._2.mkString) mkString "\n")
          BoolNodeParser.parse(data(FormulaName).mkString("")) match {
            case Some(formula) =>

              val assignments = BoolAssignment.generateAllAssignments(formula.usedVariables.toList).map(assignment =>
                assignment + (LerVariable -> (One == data(assignment.toString).mkString(""))) + (SolVariable -> formula.evaluate(assignment)))

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

  private def readFilloutQuestionFromJson(json: JsValue): Option[FilloutQuestion] = json match {
    case JsObject(values) =>

      val formula: Option[ScalaNode] = values get FormulaName flatMap {
        case JsString(formulaString) => BoolNodeParser.parse(formulaString)
        case _                       => None
      }

      val variables: Option[Seq[Char]] = values get VariablesName map {
        case JsArray(vars) => vars map {
          case JsString(str) => str(0)
          case _             => 'y'
        }
        case _             => Seq.empty
      }

      val assignments: Option[Seq[BoolAssignment]] = values get AssignmentsName map {
        case JsArray(jsAssignments) => jsAssignments.flatMap {
          case JsObject(assignmentValues) =>
            Some(new BoolAssignment(assignmentValues map {
              case (str, jsValue) =>
                (Variable(str(0)), jsValue match {
                  case JsBoolean(bool) => bool
                  case _               => false
                })
            } toMap))
          case _                          => None
        }
        case _                      => Seq.empty
      }

      println(variables)

      (formula zip assignments).headOption map {
        case (f, as) => FilloutQuestion(f, as)
      }
    case _                => None

  }

  private def checkCreationSolution(data: Map[String, Seq[String]]): Option[BooleanQuestionResult] = {
    // FIXME: get per json, read from json!
    val learnerSolution = data(FORM_VALUE).mkString
    BoolNodeParser.parse(learnerSolution) map { formula =>
      val variables = data(VARS_NAME).mkString.split(",").map(variab => Variable(variab.charAt(0))).toList

      // Check that formula only contains variables found in form
      val wrongVars = formula.usedVariables.filter(!variables.contains(_))
      //        if (wrongVars.nonEmpty)
      //          throw new CorrectionException(learnerSolution, s"In ihrer Loesung wurde(n) die folgende(n) falsche(n) Variable(n) benutzt: '${wrongVars.mkString(", ")}'")

      val assignments = BoolAssignment
        .generateAllAssignments(variables)
        .map(as => as + (LerVariable -> formula.evaluate(as)) + (SolVariable -> (data(as.toString()).mkString == One)))

      val question = new CreationQuestion(variables, assignments)
      BooleanQuestionResult(PARTIALLY, learnerSolution, question)
    }
  }

}