package controllers.exes


import java.nio.file.Files

import better.files.File._
import controllers.{AFixedExController, Secured}
import javax.inject.{Inject, Singleton}
import model.core._
import model.programming.ProgToolMain
import model.toolMains.{ASingleExerciseToolMain, ToolList}
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

  override type ToolMainType = ASingleExerciseToolMain

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  // Admin

  def adminExportExercises(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.yamlString map (yaml => Ok(views.html.admin.export(admin, yaml, toolMain, toolList)))
  }

  def adminExportExercisesAsFile(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.yamlString map { yaml =>
        val file = Files.createTempFile(s"export_${toolMain.urlPart}", ".yaml")

        file.write(yaml)

        Ok.sendPath(file, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => Files.delete(file))
      }
  }

  def adminChangeExState(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")

        Future(BadRequest("There has been an error!"))
      }

      val onFormRead: ExerciseState => Future[Result] = { newState =>
        toolMain.updateExerciseState(id, newState) map {
          case true  => Ok(Json.obj("id" -> id, "newState" -> newState.entryName))
          case false => BadRequest(Json.obj("message" -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead)
  }

  def adminExerciseList(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.futureCompleteExes map (exes => Ok(toolMain.adminExerciseList(admin, exes, toolList)))
  }

  def adminDeleteExercise(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(id) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case _ => Ok(Json.obj("id" -> id))
      }
  }

  def adminEditExerciseForm(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(id) map {
        case None           => onNoSuchExercise(id)
        case Some(exercise) => Ok(toolMain.renderAdminExerciseEditForm(admin, exercise, isCreation = false, toolList))
      }
  }

  def adminNewExerciseForm(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestId map { id =>
        val exercise = toolMain.instantiateExercise(id + 1, admin.username, ExerciseState.RESERVED)
        Ok(toolMain.renderAdminExerciseEditForm(admin, exercise, isCreation = true, toolList))
      }
  }

  def adminEditExercise(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>

      //      def onFormError: Form[toolMain.ExType] => Future[Result] = { formWithError =>
      //
      //        for (formError <- formWithError.errors)
      //          Logger.error(formError.key + " :: " + formError.message)
      //
      //        Future(BadRequest("Your form has has errors!"))
      //      }

      //      def onFormSuccess: toolMain.ExType => Future[Result] = { compEx =>
      // //        FIXME: save ex
      //        toolMain.futureUpdateExercise(compEx) map { _ =>
      //          Ok(views.html.admin.singleExercisePreview(admin, compEx, toolMain))
      //        }
      //      }

      //      toolMain.compExForm.bindFromRequest().fold(onFormError, onFormSuccess)
      ???
  }

  def adminCreateExercise(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, _) =>
    implicit request => ???
  }

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
        Ok(toolMain.renderUserExerciseEditForm(user, toolMain.compExForm.fill(exercise), isCreation = true)(request, request2Messages(request)))
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

      val onFormError: Form[toolMain.CompExType] => Future[Result] = { formWithErrors =>
        formWithErrors.errors.foreach(println)
        Future(BadRequest(toolMain.renderUserExerciseEditForm(user, formWithErrors, isCreation = true)(request, request2Messages(request))))
      }

      val onRead: toolMain.CompExType => Future[Result] = { compEx =>

        println(compEx)

        toolMain.futureInsertCompleteEx(compEx).transform {
          case Success(saved) => Success(Ok(toolMain.renderExercisePreview(user, compEx, saved)))
          case Failure(error) =>
            Logger.error("Error while saving an exercise", error)
            Success(BadRequest("Your new exercise could not be saved..."))
        }
      }

      toolMain.compExForm.bindFromRequest().fold(onFormError, onRead)
  }

  // FIXME: old class

  // Abstract types


  override protected def getToolMain(toolType: String): Option[ASingleExerciseToolMain] = toolList.getSingleExerciseToolMainOption(toolType)

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
        case None       => Future.successful(onNoSuchExercisePart(partStr))
        case Some(part) =>
          futureCompleteEx flatMap {
            case None           => Future.successful(onNoSuchExercise(id))
            case Some(exercise) =>
              toolMain.futureOldOrDefaultSolution(user, exercise.id, exercise.semanticVersion, part) map {
                oldSolution => Ok(toolMain.renderExercise(user, exercise, part, oldSolution))
              }
          }
      }
  }

  def correctLive(toolType: String, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future.successful(onNoSuchExercisePart(partStr))
        case Some(part) => toolMain.futureCompleteExById(id) flatMap {
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
      toolMain.futureCompleteExById(id) flatMap {
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
        case Some(part) => umlToolMain.futureCompleteExById(id) flatMap {
          case None                          =>
            Logger.error(s"Error while loading uml class diagram for uml exercise $id and part $part")
            Future(emptyClassDiagram)
          case Some(exercise: UmlCompleteEx) => umlToolMain.futureOldOrDefaultSolution(user, exercise.id, exercise.semanticVersion, part) map {
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
