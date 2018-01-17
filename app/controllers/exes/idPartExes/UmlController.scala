package controllers.exes.idPartExes

import javax.inject._

import controllers.Secured
import model.core._
import model.uml.UmlEnums.UmlExPart
import model.uml.UmlEnums.UmlExPart._
import model.uml.{UmlJsonProtocol, _}
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import views.html.uml._
import views.html.umlActivity._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class UmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: UmlTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[UmlExercise, UmlCompleteEx, EvaluationResult, UmlResult, UmlTableDefs](cc, dbcp, t, UmlToolObject) with JsonFormat with Secured {

  override type PartType = UmlExPart

  override def partTypeFromString(str: String): Option[UmlExPart] = UmlExPart.byString(str.toUpperCase)

  case class UmlExIdentifier(id: Int, part: UmlExPart) extends IdPartExIdentifier

  override type ExIdentifier = UmlExIdentifier

  override def identifier(id: Int, part: String): UmlExIdentifier = UmlExIdentifier(id, partTypeFromString(part) getOrElse UmlExPart.CLASS_SELECTION)

  override type SolType = UmlSolution

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[UmlSolution] =
    Solution.stringSolForm.bindFromRequest fold(_ => None, sol => UmlJsonProtocol.readFromJson(Json parse sol.learnerSolution))

  /**
    * Not yet used...
    */
  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[UmlSolution] = None

  // Yaml

  override val yamlFormat: YamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override protected def renderExercise(user: User, exercise: UmlCompleteEx, part: UmlExPart): Future[Html] = Future(part match {
    case CLASS_SELECTION   => classSelection.render(user, exercise.ex)
    case DIAG_DRAWING      => diagdrawing.render(user, exercise, getsHelp = false)
    case DIAG_DRAWING_HELP => diagdrawing.render(user, exercise, getsHelp = true)
    case ALLOCATION        => allocation.render(user, exercise)
  })

  override protected val renderExesListRest = new Html(
    s"""<div class="alert alert-info">
       |  Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
       |</div>
       |<hr>""".stripMargin)

  private def renderResult(correctionResult: UmlResult): Html = umlResult.render(correctionResult)

  override protected def renderEditRest(exercise: Option[UmlCompleteEx]): Html = editUmlExRest.render(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx, identifier: UmlExIdentifier): Try[UmlResult] = Try(identifier.part match {
    case CLASS_SELECTION   => ClassSelectionResult(exercise, sol)
    case DIAG_DRAWING_HELP => DiagramDrawingHelpResult(exercise, sol)
    case ALLOCATION        => AllocationResult(exercise, sol)
    case DIAG_DRAWING      => DiagramDrawingResult(exercise, sol)
  })

  // Other routes

  def checkSolution: EssentialAction = withAdmin { _ =>
    implicit request => {
      //      val solNode = Json.parse(singleStrForm(StringConsts.SOLUTION_NAME).get.str)
      //      JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      //        case Success(_) => Ok("Ok...")
      //        case Failure(_) => BadRequest("FEHLER!")
      //      }
      Ok("TODO")
    }
  }

  def newExerciseStep2: EssentialAction = withAdmin { _ =>
    implicit request =>
      //    exerciseReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
      //      case ReadingError(_, _, _) => BadRequest("There has been an error...")
      //      case ReadingFailure(_) => BadRequest("There has been an error...")
      //      case ReadingResult(exercises) =>
      //        val exercise = exercises.head.read.asInstanceOf[UmlExercise]
      //        val parser = new UmlExTextParser(exercise.text, exercise.mappings.asScala.toMap, exercise.ignoreWords.asScala.toList)
      //        Ok(views.html.umlAdmin.newExerciseStep2Form(user, exercise, parser.capitalizedWords.toList))
      //    }
      Ok("TODO!")
  }

  def newExerciseStep3: EssentialAction = withAdmin { _ =>
    implicit request =>
      //    exerciseReader.initFromForm(0, null /* factory.form().bindFromRequest()*/) match {
      //      case ReadingError(_, _, _) => BadRequest("There has been an error...")
      //      case ReadingFailure(_) => BadRequest("There has been an error...")
      //      case ReadingResult(exercises) =>
      //        val exercise = exercises.head.read.asInstanceOf[UmlExercise]
      //        Ok(views.html.umlAdmin.newExerciseStep3Form(user, exercise))
      //    }
      Ok("TODO!")
  }

  // Activity exercises

  def activityIndex: EssentialAction = withAdmin { user =>
    implicit request => Ok(activityDrawingIndex.render(user))
  }

  def activityExercise: EssentialAction = withAdmin { user =>
    implicit request => Ok(activitiyDrawing.render(user))
  }

  // FIXME: used where?
  def activityCheckSolution(language: String): EssentialAction = withAdmin { _ =>
    implicit request => {
      Solution.stringSolForm.bindFromRequest.fold(_ => BadRequest("TODO!"),
        solution => {
          Ok("TODO: check solution" + language + solution.learnerSolution)
        }
      )

      //      val solNode = Json.parse(singleStrForm(StringConsts.SOLUTION_NAME).get.str)
      //      JsonReader.validateJson(solNode, UmlController.SolutionSchemaNode) match {
      //        case Success(_) => Ok("Ok...")
      //        case Failure(_) => BadRequest("FEHLER!")
      //      }
    }
  }


  // Handlers for results

  protected def onSubmitCorrectionResult(user: User, result: UmlResult): Result =
    Ok(views.html.core.correction.render(result, renderResult(result), user, toolObject))

  protected def onSubmitCorrectionError(user: User, error: Throwable): Result = ???

  protected def onLiveCorrectionResult(result: UmlResult): Result = Ok(renderResult(result))

  protected def onLiveCorrectionError(error: Throwable): Result = ???

}