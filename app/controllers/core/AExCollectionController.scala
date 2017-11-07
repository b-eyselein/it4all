package controllers.core

import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.tools.ExToolObject
import model.{Exercise, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.util.Try

abstract class AExCollectionController[E <: Exercise, C <: ExerciseCollection[E], R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, t: ExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[C](cc, dbcp, r, t) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  // Admin

  //  public AExerciseCollectionAdminController(FormFactory theFactory, IdExToolObject theRoutes,
  //      Finder<Integer, E> theExerciseFinder, Finder<Integer, C> theCollectionFinder,
  //      ExerciseCollectionReader<E, C> theExerciseReader) {
  //    super(theFactory, theRoutes, theExerciseFinder,
  //        null /* theExerciseReader.getDelegateReader() */)
  //  }

  def deleteExerciseCollection(collectionId: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //      if (finder.byId(collectionId).delete())
      //        Ok("Aufgabe konnte geloescht werden!")
      //      else
      BadRequest("Konnte nicht geloescht werden!")
  }


  def exerciseCollections: EssentialAction = withAdmin { _ =>
    implicit request =>
      //      Ok(renderExerciseCollections(user, collectionFinder.all))
      Ok("TODO")
  }

  def newExerciseCollection(collectionId: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      // final C exercise = exerciseCollectionReader.initFromForm(collectionId,
      // factory.form().bindFromRequest())
      // exerciseCollectionReader.saveExercise(exercise)
      //  Ok(views.html.admin.preview.render(BaseController.user,
      // renderCollectionCreated(Arrays.asList(exercise))))
      Ok("TODO!")
  }

  def newExerciseCollectionForm: EssentialAction = withAdmin { _ =>
    implicit request =>
      //      val id = ExerciseReader.findMinimalNotUsedId(finder)
      //      collectionFinder.byId(id) match {
      //        case None             => BadRequest("")
      //        case Some(collection) => Ok(renderExCollCreationForm(user, collection))
      //      }
      Ok("TODO")
  }

  //  @Override
  //  def uploadFile() {
  //    final MultipartFormData<File> body = request().body().asMultipartFormData()
  //    final FilePart<File> uploadedFile = body.getFile(StringConsts.BODY_FILE_NAME)
  //
  //    if(uploadedFile == null)
  //       BadRequest("Fehler!")
  //
  //    final Path pathToUploadedFile = uploadedFile.getFile().toPath()
  //    final Path savingDir = Paths.get(BASE_DATA_PATH, StringConsts.ADMIN_FOLDER,
  //        exerciseCollectionReader.exerciseType())
  //
  //    final Path jsonFile = Paths.get(savingDir.toString(), uploadedFile.getFilename())
  //    final Path jsonTargetPath = saveUploadedFile(savingDir, pathToUploadedFile, jsonFile)
  //
  //    final AbstractReadingResult abstractResult = exerciseCollectionReader.readFromJsonFile(jsonTargetPath)
  //
  //    if(!(abstractResult instanceof ReadingResult<?>))
  //       BadRequest(views.html.jsonReadingError.render(BaseController.user, (ReadingError) abstractResult))
  //
  //    @SuppressWarnings("unchecked")
  //    final ReadingResult<C> result = (ReadingResult<C>) abstractResult
  //
  //    result.read().forEach(exerciseCollectionReader::save)
  //     Ok(views.html.admin.preview.render(BaseController.user, renderCollectionCreated(result.read())))
  //  }

  def renderCollectionCreated(collections: List[SingleReadingResult[C]]): Html

  def renderExEditForm(user: User, exercise: C, isCreation: Boolean): Html

  def renderExCollCreationForm(user: User, collection: C): Html

  def renderExerciseCollections(user: User, allCollections: List[C]): Html

  // User

  def exercise(id: Int): EssentialAction = withUser { _ => implicit request => Ok("TODO!") }

  def correct(id: Int): EssentialAction = withUser { _ =>
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        _ => {
          //          correctPart(solution, exerciseFinder.byId(id), part, user) match {
          //            case Success(correctionResult) =>
          //              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
          //              Ok(renderCorrectionResult(user, correctionResult))
          //            case Failure(error)            =>
          //              val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
          //              BadRequest(views.html.main.render("Fehler", user, new Html(""), content))
          //          }
          Ok("TODO")
        }
      )
  }

  def correctLive(id: Int): EssentialAction = withUser { _ =>
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        _ => {
          //          correctPart(solution, exerciseFinder.byId(id), part, user) match {
          //            case Success(correctionResult) =>
          //              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
          //              Ok(renderResult(correctionResult))
          //            case Failure(error)            => BadRequest(Json.obj("message" -> error.getMessage))
          //          }
          Ok("TODO")
        }
      )
  }

  private def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(toolObject.exType.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  def renderResult(correctionResult: CompleteResult[R]): Html

  def correctPart(form: SolutionType, exercise: Option[E], part: String, user: User): Try[CompleteResult[R]]

}
