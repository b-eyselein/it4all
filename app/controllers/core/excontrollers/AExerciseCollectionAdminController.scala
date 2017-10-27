package controllers.core.excontrollers

import controllers.core.BaseAdminController
import model.core._
import model.core.tools.IdExToolObject
import model.{Exercise, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

abstract class AExerciseCollectionAdminController[E <: Exercise, C <: ExerciseCollection[E]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, t: IdExToolObject)(implicit ec: ExecutionContext)
  extends BaseAdminController[C](cc, dbcp, r, t) with Secured {

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


  def editExercise(exerciseId: Int): EssentialAction = withAdmin { user => implicit request => ??? }

  def deleteExercise(exerciseId: Int): EssentialAction = withAdmin { user => implicit request => ??? }

  def changeExState(exerciseId: Int): EssentialAction = withAdmin { user => implicit request => ??? }

  def exerciseCollections: EssentialAction = withAdmin { user =>
    implicit request =>
      //      Ok(renderExerciseCollections(user, collectionFinder.all))
      Ok("TODO")
  }

  def exercises: EssentialAction = withAdmin { user => implicit request => ??? }

  def exportExercises: EssentialAction = withAdmin { user =>
    implicit request =>
      Ok(views.html.admin.export.render(user, Json.prettyPrint(null /*Json.toJson(finder.all)*/)))
  }

  def editExerciseForm(id: Int): EssentialAction = withAdmin { user =>
    implicit request =>
      //      collectionFinder.byId(id) match {
      //        case None           => BadRequest("")
      //        case Some(exercise) => Ok(renderExEditForm(user, exercise, isCreation = false))
      //      }
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

  def newExerciseCollectionForm: EssentialAction = withAdmin { user =>
    implicit request =>
      //      val id = ExerciseReader.findMinimalNotUsedId(finder)
      //      collectionFinder.byId(id) match {
      //        case None             => BadRequest("")
      //        case Some(collection) => Ok(renderExCollCreationForm(user, collection))
      //      }
      Ok("TODO")
  }

  def newExerciseForm: EssentialAction = withAdmin { _ =>
    implicit request =>
      //    val id = ExerciseReader.findMinimalNotUsedId(finder)
      //
      //    val exercise = exerciseReader.getOrInstantiateExercise(id)
      //    exerciseReader.save(exercise)
      //
      //    Ok(renderExEditForm(BaseController.user, exercise, true))
      Ok("TODO!")
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

}
