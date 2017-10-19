package controllers.core

import io.ebean.Finder
import model.exercise.{Exercise, ExerciseCollection}
import model.exercisereading._
import model.tools.IdExToolObject
import model.user.User
import play.data.FormFactory
import play.libs.Json
import play.mvc.Security.Authenticated
import play.mvc.{Result, Results}
import play.twirl.api.Html

import play.mvc.Results._

import scala.collection.JavaConverters._

@Authenticated(classOf[model.AdminSecured])
abstract class AExerciseCollectionAdminController[E <: Exercise, C <: ExerciseCollection[E]]
(f: FormFactory, t: IdExToolObject, fi: Finder[Integer, C], val collectionReader: ExerciseCollectionReader[E, C])
  extends BaseAdminController[C](f, t, fi, collectionReader) {

  //  public AExerciseCollectionAdminController(FormFactory theFactory, IdExToolObject theRoutes,
  //      Finder<Integer, E> theExerciseFinder, Finder<Integer, C> theCollectionFinder,
  //      ExerciseCollectionReader<E, C> theExerciseReader) {
  //    super(theFactory, theRoutes, theExerciseFinder,
  //        null /* theExerciseReader.getDelegateReader() */)
  //  }

  def deleteExerciseCollection(collectionId: Int): Result = if (finder.byId(collectionId).delete())
    ok("Aufgabe konnte geloescht werden!")
  else
    badRequest("Konnte nicht geloescht werden!")


  def editExercise(exerciseId: Int): Result = ???

  def deleteExercise(exerciseId: Int): Result = ???

  def changeExState(exerciseId: Int): Result = ???

  def exerciseCollections: Result = ok(renderExerciseCollections(getUser, finder.all.asScala.toList))

  def exercises: Result = ???

  def exportExercises: Result =
    ok(views.html.admin.export.render(getUser, Json.prettyPrint(Json.toJson(finder.all))))

  def importExercises: Result = collectionReader.readFromJsonFile() match {
    case error: ReadingError =>
      badRequest(views.html.jsonReadingError.render(getUser, error))

    case _: ReadingFailure => badRequest("There has been an error...")

    case result: ReadingResult[C] =>
      result.read.foreach(read => {
        collectionReader.save(read.read)
        read.fileResults = collectionReader.checkFiles(read.read)
      })
      ok(views.html.admin.preview.render(getUser, toolObject, List.empty /*result.read*/))
  }

  //  def importExercises: Result = processReadingResult(collectionReader.readFromJsonFile(), renderCollectionCreated)


  def uploadFile: Result = super.uploadFile(_ => new Html(""))

  def editExerciseForm(id: Int): Result = Option(finder.byId(id)) match {
    case None => badRequest("")
    case Some(exercise) => ok(renderExEditForm(getUser, exercise, isCreation = false))
  }

  def newExerciseCollection(collectionId: Int): Result = {
    // final C exercise = exerciseCollectionReader.initFromForm(collectionId,
    // factory.form().bindFromRequest())
    // exerciseCollectionReader.saveExercise(exercise)
    //  ok(views.html.admin.preview.render(BaseController.getUser,
    // renderCollectionCreated(Arrays.asList(exercise))))
    ok("TODO!")
  }

  def newExerciseCollectionForm: Result = {
    val id = ExerciseReader.findMinimalNotUsedId(finder)
    val collection = finder.byId(id)
    ok(renderExCollCreationForm(getUser, collection))
  }

  def newExerciseForm: Result = {
    //    val id = ExerciseReader.findMinimalNotUsedId(finder)
    //
    //    val exercise = exerciseReader.getOrInstantiateExercise(id)
    //    exerciseReader.save(exercise)
    //
    //    ok(renderExEditForm(BaseController.getUser, exercise, true))
    ok("TODO!")
  }

  //  @Override
  //  def uploadFile() {
  //    final MultipartFormData<File> body = request().body().asMultipartFormData()
  //    final FilePart<File> uploadedFile = body.getFile(StringConsts.BODY_FILE_NAME)
  //
  //    if(uploadedFile == null)
  //       badRequest("Fehler!")
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
  //       badRequest(views.html.jsonReadingError.render(BaseController.getUser, (ReadingError) abstractResult))
  //
  //    @SuppressWarnings("unchecked")
  //    final ReadingResult<C> result = (ReadingResult<C>) abstractResult
  //
  //    result.read().forEach(exerciseCollectionReader::save)
  //     ok(views.html.admin.preview.render(BaseController.getUser, renderCollectionCreated(result.read())))
  //  }

  def renderCollectionCreated(collections: List[SingleReadingResult[C]]): Html

  def renderExEditForm(user: User, exercise: C, isCreation: Boolean): Html

  def renderExCollCreationForm(user: User, collection: C): Html

  def renderExerciseCollections(user: User, allCollections: List[C]): Html

}
