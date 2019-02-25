package controllers.exes


import controllers.{AFixedExController, Secured}
import javax.inject.{Inject, Singleton}
import model.core._
import model.programming.ProgToolMain
import model.toolMains.{ASingleExerciseToolMain, SingleExerciseIdentifier, ToolList}
import model.uml._
import model.web.{WebExParts, WebToolMain}
import model.{ExerciseState, SemanticVersion, SemanticVersionHelper}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.single
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val repository: Repository, ws: WSClient, tl: ToolList,
                                   progToolMain: ProgToolMain, umlToolMain: UmlToolMain, webToolMain: WebToolMain)
                                  (implicit ec: ExecutionContext) extends AFixedExController(cc, dbcp, tl) with Secured with play.api.i18n.I18nSupport {

  // FIXME: old class ASingleExerciseController

  override protected type ToolMainType = ASingleExerciseToolMain

  override protected val adminRightsRequired: Boolean = false

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  // Views

  def exerciseList(toolType: String, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.dataForUserExesOverview(user, page) map {
        dataForUserExesOverview => Ok(views.html.exercises.userExercisesOverview(user, dataForUserExesOverview, toolMain, page))
      }
  }

  def newExerciseForm(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    request =>
      // FIXME: reserve exercise... ?
      toolMain.futureHighestId map { id =>
        val exercise = toolMain.instantiateExercise(id + 1, user.username, ExerciseState.CREATED)
        Ok(toolMain.renderUserExerciseEditForm(user, toolMain.exerciseForm.fill(exercise), isCreation = true)(request, request2Messages(request)))
      }
  }

  def editExercise(toolType: String, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      //      def onFormError: Form[toolMain.ExType] => Future[Result] = { formWithError =>
      //
      //        for (formError <- formWithError.errors)
      //          Logger.error(formError.key + " :: " + formError.message)
      //
      //        Future(BadRequest("Your form has has errors!"))
      //      }
      //
      //      def onFormSuccess: toolMain.ExType => Future[Result] = { compEx =>
      //        //FIXME: save ex
      //        ???
      //
      //        toolMain.futureUpdateExercise(compEx) map { _ =>
      //          Ok(views.html.admin.singleExercisePreview(user, compEx, toolMain))
      //        }
      //      }

      //      toolMain.compExForm.bindFromRequest().fold(onFormError, onFormSuccess)
      ???
  }

  def newExercise(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val onFormError: Form[toolMain.ExType] => Future[Result] = { formWithErrors =>
        formWithErrors.errors.foreach(println)
        Future(BadRequest(toolMain.renderUserExerciseEditForm(user, formWithErrors, isCreation = true)(request, request2Messages(request))))
      }

      val onRead: toolMain.ExType => Future[Result] = { compEx =>

        println(compEx)

        toolMain.futureInsertExercise(compEx).transform {
          case Success(saved) => Success(Ok(toolMain.renderExercisePreview(user, compEx, saved)))
          case Failure(error) =>
            Logger.error("Error while saving an exercise", error)
            Success(BadRequest("Your new exercise could not be saved..."))
        }
      }

      toolMain.exerciseForm.bindFromRequest().fold(onFormError, onRead)
  }

  // FIXME: old class

  // Abstract types

  override protected def getToolMain(toolType: String): Option[ASingleExerciseToolMain] = toolList.getSingleExerciseToolMainOption(toolType)

  // Generic Routes

  def exercise(toolType: String, id: Int, partStr: String, versionStr: String = "latest"): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val requestedVersion: Option[SemanticVersion] = versionStr match {
        case "latest" => None
        case _        => SemanticVersionHelper.parseFromString(versionStr)
      }

      def futureCompleteEx: Future[Option[toolMain.ExType]] = requestedVersion match {
        case Some(version) => toolMain.futureExerciseByIdAndVersion(id, version)
        case None          => toolMain.futureExerciseById(id)
      }

      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future.successful(onNoSuchExercisePart(partStr))
        case Some(part) =>
          futureCompleteEx flatMap {
            case None           => Future.successful(onNoSuchExercise(id))
            case Some(exercise) =>
              val exIdentifier = SingleExerciseIdentifier(exercise.id, exercise.semanticVersion)
              toolMain.futureMaybeOldSolution(user, exIdentifier, part) map {
                oldSolution => Ok(toolMain.renderExercise(user, exercise, part, oldSolution))
              }
          }
      }
  }

  def correctLive(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future.successful(onNoSuchExercisePart(partStr))
        case Some(part) => toolMain.futureExerciseById(id) flatMap {
          case None               => Future.successful(onNoSuchExercise(id))
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
        case None       => Future.successful(onNoSuchExercisePart(partStr))
        case Some(part) =>
          toolMain.futureExerciseById(id) map {
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
        case Some(part) => toolMain.futureExerciseById(id) flatMap {
          case None                   => Future(onNoSuchExercise(id))
          case Some(completeExercise) =>
            val onFormError: Form[toolMain.ReviewType] => Future[Result] = { formWithErrors =>
              ???
            }

            val onFormRead: toolMain.ReviewType => Future[Result] = { currentReview =>
              toolMain.futureSaveReview(currentReview) map {
                case true  => Redirect(controllers.routes.MainExerciseController.index(toolMain.urlPart))
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
      toolMain.futureExerciseById(id) flatMap {
        case None    => Future(onNoSuchExercise(id))
        case Some(_) => toolMain.futureReviewsForExercise(id) map {
          reviews => Ok(views.html.admin.idExes.idExerciseReviewListExercise(admin, reviews, toolList, toolMain))
        }
      }
  }

  def sampleSolutionsForPart(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) => toolMain.futureSampleSolutionsForExerciseAndPart(id, part) map {
          case Seq()                  => NotFound("Could not find a sample solution...")
          case solutions: Seq[String] => Ok(JsArray(solutions map JsString.apply))
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
        case Some(part) => umlToolMain.futureExerciseById(id) flatMap {
          case None                        =>
            Logger.error(s"Error while loading uml class diagram for uml exercise $id and part $part")
            Future(emptyClassDiagram)
          case Some(exercise: UmlExercise) =>
            val exIdentifier = SingleExerciseIdentifier(exercise.id, exercise.semanticVersion)

            umlToolMain.futureMaybeOldSolution(user, exIdentifier, part) map {
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
      progToolMain.futureExerciseById(id) map {
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
          webToolMain.writeWebSolutionFile(user.username, id, webToolMain.partTypeFromUrl(part).getOrElse(WebExParts.HtmlPart), text)
          //          match {
          //            case Success(_)     =>
          Ok("Solution saved")
        //            case Failure(error) =>
        //              Logger.error("Error while updating web solution", error)
        //              BadRequest("Solution was not saved!")
        //          }
      }
  }

}
