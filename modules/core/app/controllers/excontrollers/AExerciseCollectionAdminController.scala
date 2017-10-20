package controllers.excontrollers

import controllers.core.BaseAdminController
import io.ebean.Finder
import model.exercise.{Exercise, ExerciseCollection}
import model.exercisereading._
import model.tools.IdExToolObject
import model.user.User
import play.api.mvc.ControllerComponents
import play.api.libs.json.Json
import play.mvc.Security.Authenticated
import play.twirl.api.Html

import scala.collection.JavaConverters._

@Authenticated(classOf[model.AdminSecured])
abstract class AExerciseCollectionAdminController[E <: Exercise, C <: ExerciseCollection[E]]
(cc: ControllerComponents, t: IdExToolObject, fi: Finder[Integer, C], val collectionReader: ExerciseCollectionReader[E, C])
  extends BaseAdminController[C](cc, t, fi, collectionReader) {

  //  public AExerciseCollectionAdminController(FormFactory theFactory, IdExToolObject theRoutes,
  //      Finder<Integer, E> theExerciseFinder, Finder<Integer, C> theCollectionFinder,
  //      ExerciseCollectionReader<E, C> theExerciseReader) {
  //    super(theFactory, theRoutes, theExerciseFinder,
  //        null /* theExerciseReader.getDelegateReader() */)
  //  }

  def deleteExerciseCollection(collectionId: Int) = Action { implicit request =>
    if (finder.byId(collectionId).delete())
      Ok("Aufgabe konnte geloescht werden!")
    else
      BadRequest("Konnte nicht geloescht werden!")
  }


  def editExercise(exerciseId: Int) = Action { implicit request => ??? }

  def deleteExercise(exerciseId: Int) = Action { implicit request => ??? }

  def changeExState(exerciseId: Int) = Action { implicit request => ??? }

  def exerciseCollections = Action { implicit request =>
    Ok(renderExerciseCollections(getUser, finder.all.asScala.toList))
  }

  def exercises = Action { implicit request => ??? }

  def exportExercises = Action { implicit request =>
    Ok(views.html.admin.export.render(getUser, Json.prettyPrint(null /*Json.toJson(finder.all)*/)))
  }

  def importExercises = Action { implicit request =>
    collectionReader.readFromJsonFile() match {
      case error: ReadingError =>
        BadRequest(views.html.jsonReadingError.render(getUser, error))

      case _: ReadingFailure => BadRequest("There has been an error...")

      case result: ReadingResult[C] =>
        result.read.foreach(read => {
          collectionReader.save(read.read)
          read.fileResults = collectionReader.checkFiles(read.read)
        })
        Ok(views.html.admin.preview.render(getUser, toolObject, List.empty /*result.read*/))
    }
  }

  //  def importExercises: Result = processReadingResult(collectionReader.readFromJsonFile(), renderCollectionCreated)


  def uploadFile = super.uploadFile(_ => new Html(""))

  def editExerciseForm(id: Int) = Action { implicit request =>
    Option(finder.byId(id)) match {
      case None => BadRequest("")
      case Some(exercise) => Ok(renderExEditForm(getUser, exercise, isCreation = false))
    }
  }

  def newExerciseCollection(collectionId: Int) = Action { implicit request =>
    // final C exercise = exerciseCollectionReader.initFromForm(collectionId,
    // factory.form().bindFromRequest())
    // exerciseCollectionReader.saveExercise(exercise)
    //  Ok(views.html.admin.preview.render(BaseController.getUser,
    // renderCollectionCreated(Arrays.asList(exercise))))
    Ok("TODO!")
  }

  def newExerciseCollectionForm = Action { implicit request =>
    val id = ExerciseReader.findMinimalNotUsedId(finder)
    val collection = finder.byId(id)
    Ok(renderExCollCreationForm(getUser, collection))
  }

  def newExerciseForm = Action { implicit request =>
    //    val id = ExerciseReader.findMinimalNotUsedId(finder)
    //
    //    val exercise = exerciseReader.getOrInstantiateExercise(id)
    //    exerciseReader.save(exercise)
    //
    //    Ok(renderExEditForm(BaseController.getUser, exercise, true))
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
  //       BadRequest(views.html.jsonReadingError.render(BaseController.getUser, (ReadingError) abstractResult))
  //
  //    @SuppressWarnings("unchecked")
  //    final ReadingResult<C> result = (ReadingResult<C>) abstractResult
  //
  //    result.read().forEach(exerciseCollectionReader::save)
  //     Ok(views.html.admin.preview.render(BaseController.getUser, renderCollectionCreated(result.read())))
  //  }

  def renderCollectionCreated(collections: List[SingleReadingResult[C]]): Html

  def renderExEditForm(user: User, exercise: C, isCreation: Boolean): Html

  def renderExCollCreationForm(user: User, collection: C): Html

  def renderExerciseCollections(user: User, allCollections: List[C]): Html

}
