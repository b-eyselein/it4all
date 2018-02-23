package controllers.exes.idPartExes

import controllers.Secured
import javax.inject._
import model.core._
import model.uml._
import model.yaml.MyYamlFormat
import model.{JsonFormat, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import scalatags.Text.all._
import views.html.uml._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class UmlController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, protected val tables: UmlTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[UmlExPart](cc, dbcp, UmlToolObject) with JsonFormat with Secured {

  override def partTypeFromUrl(urlName: String): Option[UmlExPart] = UmlExParts.values.find(_.urlName == urlName)

  // Result types

  override type ExType = UmlExercise

  override type CompExType = UmlCompleteEx

  override type Tables = UmlTableDefs

  override type R = EvaluationResult

  override type CompResult = UmlResult

  // Reading solution

  override type SolType = UserUmlSolution

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[UserUmlSolution] = {
    Solution.stringSolForm.bindFromRequest fold(
      _ => None,
      sol => {
        //        println(sol)
        UmlJsonProtocol.readUserUmlSolutionFromJson(Json parse sol.learnerSolution)
      })
  }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: UmlExPart): Option[UserUmlSolution] = ???

  // Yaml

  override val yamlFormat: MyYamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override protected def renderExercise(user: User, exercise: UmlCompleteEx, part: UmlExPart): Future[Html] = Future(part match {
    case ClassSelection     => classSelection.render(user, exercise.ex)
    case DiagramDrawing     => diagdrawing.render(user, exercise, getsHelp = false)
    case DiagramDrawingHelp => diagdrawing.render(user, exercise, getsHelp = true)
    case MemberAllocation   => allocation.render(user, exercise)
  })

  override protected val renderExesListRest = new Html(
    s"""<div class="alert alert-info">
       |  Neueinsteiger sollten die Variante mit Zwischenkorrektur verwenden, die die einzelnen Schritte der Erstellung eines Klassendiagrammes nach und nach durcharbeitet.
       |</div>
       |<hr>""".stripMargin)

  private def renderResult(corResult: UmlResult): Html = {

    val resultsRender: String = corResult.notEmptyMatchingResults map (_.describe) mkString "\n"

    val nextPartLink: String = corResult.nextPart match {
      case Some(np) =>
        a(href := routes.UmlController.exercise(corResult.exercise.ex.id, np.urlName).url, cls := "btn btn-primary btn-block")("Zum nächsten Aufgabenteil").toString
      case None     =>
        a(href := routes.UmlController.index().url, cls := "btn btn-primary btn-block")("Zurück zur Startseite").toString
    }

    Html(resultsRender + "<hr>" + nextPartLink)
  }

  override protected def renderEditRest(exercise: Option[UmlCompleteEx]): Html = editUmlExRest.render(exercise)

  // Correction

  override def correctEx(user: User, sol: UserUmlSolution, exercise: UmlCompleteEx): Future[Try[UmlResult]] = Future {
    Try {
      sol.forPart match {
        case ClassSelection     => ClassSelectionResult(exercise, sol.solution)
        case DiagramDrawing     => DiagramDrawingResult(exercise, sol.solution)
        case DiagramDrawingHelp => DiagramDrawingHelpResult(exercise, sol.solution)
        case MemberAllocation   => AllocationResult(exercise, sol.solution)
      }
    }
  }

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

  // Handlers for results

  protected def onSubmitCorrectionResult(user: User, result: UmlResult): Html = views.html.core.correction.render(result, renderResult(result), user, toolObject)

  protected def onSubmitCorrectionError(user: User, error: Throwable): Html = {

    views.html.core.correctionError.render(user, OtherCorrectionException(error))
    //    error match {
    //      case NoSuchExerciseException(exId) =>
    //        Logger.error(s"Trying to sumit a correction for exercise $exId!")
    //        NotFound(s"There is no exercise with id $exId")
    //
    //      case SolutionTransferException =>
    //        Logger.error("There has been an error transfering a solution")
    //        BadRequest("There has been an error transfering a solution")
    //
    //      case OtherCorrectionException(cause) =>
    //        Logger.error(error.getMessage, cause)
    //        BadRequest(views.html.core.correctionError.render(user, error))
    //
    //      case _ => new Html("TODO!")
    //    }
  }

  protected def onLiveCorrectionResult(result: UmlResult): JsValue = ??? // Ok(renderResult(result))


}