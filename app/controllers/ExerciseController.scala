package controllers

import javax.inject.{Inject, Singleton}
import model.core._
import model.programming.ProgToolMain
import model.toolMains.{IdExerciseToolMain, ToolList}
import model.uml._
import model.web.{WebExParts, WebToolMain}
import model.{SemanticVersion, SemanticVersionHelper}
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val repository: Repository, ws: WSClient, tl: ToolList,
                                   progToolMain: ProgToolMain, umlToolMain: UmlToolMain, webToolMain: WebToolMain)
                                  (implicit ec: ExecutionContext) extends ASingleExerciseController(cc, dbcp, tl) with Secured {

  // Abstract types

  override type ToolMainType = IdExerciseToolMain

  override protected def getToolMain(toolType: String): Option[IdExerciseToolMain] = toolList.getExerciseToolMainOption(toolType)

  // Generic results

  protected def onNoSuchExercisePart(partStr: String): Result = NotFound(s"Es gibt keine Aufgabenteil '$partStr'")


  // Generic Routes

  def exercise(toolType: String, id: Int, partStr: String, versionStr: String = "latest"): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val requestedVersion: Option[SemanticVersion] = versionStr match {
        case "latest" => None
        case _        => SemanticVersionHelper.parseFromString(versionStr)
      }

      def futureCompleteEx: Future[Option[toolMain.CompExType]] = requestedVersion match {
        case Some(version) => toolMain.futureCompleteExByIdAndVersion(id, version)
        case None          => toolMain.futureCompleteExById(id)
      }

      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) =>
          futureCompleteEx flatMap {
            case None           => Future(onNoSuchExercise(id))
            case Some(exercise) =>
              toolMain.futureOldOrDefaultSolution(user, exercise.ex.id, exercise.ex.semanticVersion, part) map {
                oldSolution => Ok(toolMain.renderExercise(user, exercise, part, oldSolution))
              }
          }
      }
  }

  def correctLive(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) => toolMain.futureCompleteExById(id) flatMap {
          case None               => Future(onNoSuchExercise(id))
          case Some(compExercise) => toolMain.correctAbstract(user, compExercise, part) map {
            case Success(result) => Ok(result)
            case Failure(error)  =>
              Logger.error("There has been an internal correction error:", error)
              BadRequest(toolMain.onLiveCorrectionError(error))
          }
        }
      }
  }

  def reviewExercisePartForm(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) =>
          toolMain.futureCompleteExById(id) map {
            case None               => onNoSuchExercise(id)
            case Some(compExercise) => Ok(toolMain.renderExerciseReviewForm(user, compExercise, part))
            //          Future(Ok(views.html.idExercises.xml.evaluateExerciseForm(user, toolMain, )))
          }
      }
  }


  def reviewExercisePart(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) => toolMain.futureCompleteExById(id) flatMap {
          case None                   => Future(onNoSuchExercise(id))
          case Some(completeExercise) =>
            val onFormError: Form[toolMain.ReviewType] => Future[Result] = { formWithErrors =>
              ???
            }

            val onFormRead: toolMain.ReviewType => Future[Result] = { currentReview =>
              toolMain.futureSaveReview(currentReview) map {
                case true  => Redirect(routes.MainExerciseController.index(toolMain.urlPart))
                case false => ???
              }
            }

            toolMain.exerciseReviewForm(user.username, completeExercise, part).bindFromRequest().fold(onFormError, onFormRead)
        }
      }
  }

  def exerciseReviewsList(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureAllReviews map {
        allReviews => Ok(views.html.admin.idExes.idExerciseReviewsList(admin, allReviews, toolList, toolMain))
      }
  }

  def showReviews(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(id) flatMap {
        case None    => Future(onNoSuchExercise(id))
        case Some(_) => toolMain.futureReviewsForExercise(id) map {
          reviews => Ok(views.html.admin.idExes.idExerciseReviewListExercise(admin, reviews, toolList, toolMain))
        }
      }
  }

  def sampleSolutionForPart(toolType: String, id: Int, partStr: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) => toolMain.futureSampleSolutionForExerciseAndPart(id, part) map {
          case None      => NotFound("Could not find a sample solution...")
          case Some(sol) => Ok(sol)
        }
      }
  }

  def showSolutions(toolType: String, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureSolutionsForExercise(id) map {
        solutions => Ok(views.html.admin.idExes.idExerciseSolutions(user, solutions, toolList, toolMain))
      }
  }

  // Other routes

  def umlClassDiag(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      def emptyClassDiagram: UmlClassDiagram = UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]())

      val futureClassDiagram: Future[UmlClassDiagram] = umlToolMain.partTypeFromUrl(partStr) match {
        case None       => Future(emptyClassDiagram)
        case Some(part) => umlToolMain.futureCompleteExById(id) flatMap {
          case None                          =>
            Logger.error(s"Error while loading uml class diagram for uml exercise $id and part $part")
            Future(emptyClassDiagram)
          case Some(exercise: UmlCompleteEx) => umlToolMain.futureOldOrDefaultSolution(user, exercise.ex.id, exercise.ex.semanticVersion, part) map {
            case Some(solution) => solution.solution
            case None           => exercise.getDefaultClassDiagForPart(part)
          }
        }
      }

      futureClassDiagram map { classDiagram =>
        Ok(Json.prettyPrint(UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(classDiagram))).as("text/javascript")
      }
  }

  def progClassDiagram(id: Int): EssentialAction = futureWithUser { _ =>
    implicit request =>
      progToolMain.futureCompleteExById(id) map {
        case None           => onNoSuchExercise(id)
        case Some(exercise) =>
          val jsValue = exercise.maybeClassDiagramPart match {
            case Some(cd) => Json.toJson(cd.umlClassDiagram)(UmlClassDiagramJsonFormat.umlSolutionJsonFormat)
            case None     => JsObject.empty
          }
          Ok(jsValue) //.as("text/javascript")
      }
  }

  def webSolution(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      webToolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) => ws.url(webToolMain.getSolutionUrl(user, id, part)).get() map (wsRequest => Ok(wsRequest.body).as("text/html"))
      }
  }

  def updateWebSolution(id: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      request.body.asText match {
        case None       => BadRequest("No content!")
        case Some(text) =>
          webToolMain.writeWebSolutionFile(user.username, id, webToolMain.partTypeFromUrl(part).getOrElse(WebExParts.HtmlPart), text) match {
            case Success(_)     => Ok("Solution saved")
            case Failure(error) =>
              Logger.error("Error while updating web solution", error)
              BadRequest("Solution was not saved!")
          }
      }
  }

}
