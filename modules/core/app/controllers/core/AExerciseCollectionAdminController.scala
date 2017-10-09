package controllers.core

import java.io.File
import java.nio.file.Paths

import scala.collection.JavaConverters._

import io.ebean.Finder
import model.StringConsts
import model.exercise.{ Exercise, ExerciseCollection }
import model.user.User
import play.data.FormFactory
import play.libs.Json
import play.mvc.Controller
import play.mvc.Http.MultipartFormData
import play.mvc.Http.MultipartFormData.FilePart
import play.mvc.Results
import play.mvc.Security.Authenticated
import play.twirl.api.Html
import scala.util.Failure
import scala.util.Success
import play.mvc.Result
import model.exercisereading.ExerciseCollectionReader
import model.exercisereading.ExerciseReader

@Authenticated(classOf[model.AdminSecured])
abstract class AExerciseCollectionAdminController[E <: Exercise, C <: ExerciseCollection[E]](
  f: FormFactory, val finder: Finder[Integer, C], val collectionReader: ExerciseCollectionReader[E, C])
  extends BaseAdminController[C](f, collectionReader) {

  //  public AExerciseCollectionAdminController(FormFactory theFactory, IdExToolObject theRoutes,
  //      Finder<Integer, E> theExerciseFinder, Finder<Integer, C> theCollectionFinder,
  //      ExerciseCollectionReader<E, C> theExerciseReader) {
  //    super(theFactory, theRoutes, theExerciseFinder,
  //        null /* theExerciseReader.getDelegateReader() */)
  //  }

  def deleteExerciseCollection(collectionId: Int) = {
    if (finder.byId(collectionId).delete())
      Results.ok("Aufgabe konnte gelöscht werden!")
    else
      Results.badRequest("Konnte nicht gelöscht werden!")
  }

  def editExercise(exerciseId: Int): Result = ???

  def deleteExercise(exerciseId: Int): Result = ???

  def changeExState(exerciseId: Int): Result = ???

  def exerciseCollections = Results.ok(renderExerciseCollections(BaseController.getUser, finder.all))
  
  def exercises: Result = ???

  def exportExercises =
    Results.ok(views.html.admin.export.render(BaseController.getUser, Json.prettyPrint(Json.toJson(finder.all))))

  def importExercises = processReadingResult(collectionReader.readFromJsonFile(), renderCollectionCreated(_, _))

  def editExerciseForm(id: Int) = finder.byId(id) match {
    case exercise if exercise == null => Results.badRequest("")
    case exercise                     => Results.ok(renderExEditForm(BaseController.getUser, exercise, false))
  }

  def newExerciseCollection(collectionId: Int) =  {
    // final C exercise = exerciseCollectionReader.initFromForm(collectionId,
    // factory.form().bindFromRequest())
    // exerciseCollectionReader.saveExercise(exercise)
    // Results. ok(views.html.admin.preview.render(BaseController.getUser,
    // renderCollectionCreated(Arrays.asList(exercise))))
    Results.ok("TODO!")
  }

  def newExerciseCollectionForm = {
    val id = ExerciseReader.findMinimalNotUsedId(finder)
    val collection = finder.byId(id)
    Results.ok(renderExCollCreationForm(BaseController.getUser, collection))
  }

  def newExerciseForm = {
    val id = ExerciseReader.findMinimalNotUsedId(finder)

//    val exercise = exerciseReader.getOrInstantiateExercise(id)
//    exerciseReader.save(exercise)
//
//    Results.ok(renderExEditForm(BaseController.getUser, exercise, true))
    Results.ok("TODO!")
  }

  //  @Override
  //  def uploadFile() {
  //    final MultipartFormData<File> body = request().body().asMultipartFormData()
  //    final FilePart<File> uploadedFile = body.getFile(StringConsts.BODY_FILE_NAME)
  //
  //    if(uploadedFile == null)
  //      Results. badRequest("Fehler!")
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
  //      Results. badRequest(views.html.jsonReadingError.render(BaseController.getUser, (ReadingError) abstractResult))
  //
  //    @SuppressWarnings("unchecked")
  //    final ReadingResult<C> result = (ReadingResult<C>) abstractResult
  //
  //    result.read().forEach(exerciseCollectionReader::save)
  //    Results. ok(views.html.admin.preview.render(BaseController.getUser, renderCollectionCreated(result.read())))
  //  }

  def uploadFile: Result = uploadFile(renderCollectionCreated(_, _))

  def getSampleDir = Paths.get(BaseController.BASE_DATA_PATH, BaseController.SAMPLE_SUB_DIRECTORY,
                               collectionReader.exerciseType)

  def renderCollectionCreated(collections: java.util.List[C], created: Boolean): Html

  def renderExEditForm(user: User, exercise: C, isCreation: Boolean): Html

  def renderExCollCreationForm(user: User, collection: C): Html

  def renderExerciseCollections(user: User, allCollections: java.util.List[C]): Html

}
