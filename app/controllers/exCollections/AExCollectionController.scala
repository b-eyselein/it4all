package controllers.exCollections

import java.nio.file.{Files, Paths}
import java.sql.SQLSyntaxErrorException

import controllers.Secured
import model._
import model.core.CoreConsts._
import model.core._
import model.core.tools.ExToolObject
import net.jcazevedo.moultingyaml._
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

trait CollectionToolObject extends ExToolObject with FileUtils {

  val collectionName: String

  def exerciseRoute(exercise: CompEx): Call

  def correctLiveRoute(exercise: HasBaseValues): Call

  def correctRoute(exercise: HasBaseValues): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = Map(exerciseRoute(exercise) -> "Aufgabe bearbeiten")

}

abstract class AExCollectionController[E <: Exercise, C <: ExerciseCollection[E, _], R <: EvaluationResult]
(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository, val toolObject: CollectionToolObject)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

  // Reading solution from Request

  type SolutionType <: Solution

  def solForm: Form[SolutionType]

  // Reading Yaml

  type CompColl <: CompleteCollection

  implicit val yamlFormat: YamlFormat[CompColl]

  // Database queries

  import profile.api._

  protected type TQ <: repo.HasBaseValuesTable[C]

  protected def tq: TableQuery[TQ]

  protected def numOfExes: Future[Int] = db.run(tq.size.result)

  protected def completeCollById(id: Int): Future[Option[CompColl]] = ???

  protected def completeColls: Future[Seq[CompColl]]

  protected def statistics: Future[Html] = numOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  protected def saveRead(read: Seq[CompColl]): Future[Seq[Int]]

  // Reading from form TODO: Move to other file?

  case class StrForm(str: String)

  def singleStrForm(str: String) = Form(mapping(str -> nonEmptyText)(StrForm.apply)(StrForm.unapply))

  //  protected val savingDir: Path = Paths.get(toolObject.rootDir, ADMIN_FOLDER, toolObject.exType)

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  //  private def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] =
  //    Try(Files.createDirectories(savingDir))
  //      .map(_ => Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.username} - ${Json.toJson(eventToLog)}""")

  // Admin

  def adminIndex: EssentialAction = futureWithAdmin { user =>
    implicit request => statistics map (stats => Ok(views.html.admin.adminMain.render(user, stats, toolObject, new Html(""))))
  }

  def adminImportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = Paths.get("conf", "resources", toolObject.exType + ".yaml").toFile
      val read = Source.fromFile(file).mkString.parseYamls map (_.convertTo[CompColl])
      saveAndPreviewExercises(admin, read)
  }

  /**
    * FIXME: save and import in same method? => in direct subclasses!?!
    *
    * @param admin current user, has to be admin
    * @param read  Seq of read (exercises)
    * @return
    */
  def saveAndPreviewExercises(admin: User, read: Seq[CompColl]): Future[Result] =
    saveRead(read) map (_ => Ok(previewExercises(admin, read))) recover {
      // FIXME: Failures!
      case sqlError: SQLSyntaxErrorException =>
        sqlError.printStackTrace()
        BadRequest(sqlError.getMessage)
      case throwable: Throwable              =>
        println("\nERROR: ")
        throwable.printStackTrace()
        BadRequest(throwable.getMessage)
    }

  def adminExportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      completeColls map (exes => Ok(views.html.admin.export.render(admin, yamlString(exes), null /* toolObject*/)))
  }

  def adminExportExercisesAsFile: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = Files.createTempFile(s"export_${toolObject.exType}", ".yaml")
      completeColls map (exes => {

        write(file, yamlString(exes))

        Ok.sendPath(file, fileName = _ => s"export_${toolObject.exType}.yaml", onClose = () => Files.delete(file))
      })
  }

  def adminChangeExState(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //      val newState: ExerciseState = Option(ExerciseState.valueOf(singleStrForm("state").bindFromRequest.get.str)).getOrElse(ExerciseState.RESERVED)
      //
      //
      //      val updateAction = (for {ex <- tq if ex.id === id} yield ex.state).update(newState)
      //
      //      db.run(updateAction).map {
      //        case 1     => Ok("TODO!")
      //        case other => BadRequest("")
      //      }
      //      completeExById(id).map {
      //        case None           => BadRequest(Json.obj("message" -> "No such file exists..."))
      //        case Some(exercise) =>
      //          exercise.state =
      //          //          //          exercise.save()
      //          //          Ok(Json.obj("id" -> id, "newState" -> exercise.state.toString))
      Ok("TODO")
    //      }
  }

  def adminDeleteExercise(id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      db.run(tq.filter(_.id === id).delete) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case _ => Ok(Json.obj("id" -> id))
      }
  }

  def adminEditExercise(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //    exerciseReader.initFromForm(id, factory.form().bindFromRequest()) match {
      //      case error: ReadingError =>
      //        BadRequest(views.html.jsonReadingError.render(admin, error))
      //      case _: ReadingFailure => BadRequest("There has been an error...")
      //      case result: ReadingResult[E] =>
      //
      //        result.read.foreach(res => exerciseReader.save(res.read))
      //        Ok(views.html.admin.exPreview.render(admin, toolObject, result.read))
      //    }
      Ok("TODO!")
  }

  def adminEditExerciseForm(id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request => completeCollById(id) map (ex => Ok("TODO!" /*views.html.admin.editExForm(admin, toolObject, ex, renderEditRest(ex))*/))
  }

  def adminExerciseList: EssentialAction = futureWithAdmin { admin =>
    implicit request => completeColls map (colls => Ok(views.html.admin.collectionList.render(admin, colls, toolObject)))
  }

  def adminNewExerciseForm: EssentialAction = withAdmin { admin =>
    // FIXME
    implicit request =>
      // FIXME: ID of new exercise?
      Ok(views.html.admin.editExForm.render(admin, null /*toolObject */ , None, renderEditRest(None)))
  }

  def newExercise: EssentialAction = withAdmin { admin =>
    implicit request => ???
    //      Ok("TODO: Not yet used...")
  }

  // Views and other helper methods for admin

  protected def previewExercises(admin: User, read: Seq[CompColl]): Html = views.html.admin.collPreview(admin, read, toolObject)

  // FIXME: scalarStyle = Folded if fixed...
  private def yamlString(exes: Seq[CompColl]): String = "%YAML 1.2\n---\n" + (exes map (_.toYaml.print(Auto /*, Folded*/)) mkString "---\n")

  // User

  def index: EssentialAction = exerciseList(page = 1)

  def exerciseList(page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeColls map (allExes => {
        val exes = allExes slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size))
        Ok(renderExes(user, exes, allExes.size))
      })
  }

  // FIXME: refactor...
  protected def renderExes(user: User, colls: Seq[CompColl], allExesSize: Int): Html =
    views.html.core.collsList.render(user, colls, renderExesListRest, toolObject, allExesSize / STEP + 1)


  /**
    * Used for rendering things such as playgrounds
    *
    * @return Html - with link to other "exercises"
    */
  protected def renderExesListRest: Html = new Html("")

  // Helper methods

  def renderEditRest(exercise: Option[CompColl]): Html = ???

  def renderExercises(exercises: List[CompColl]): Html = ??? //views.html.admin.exercisesTable.render(exercises, null /*toolObject */)


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
      //  Ok(views.html.admin.exPreview.render(BaseController.user,
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
  //     Ok(views.html.admin.exPreview.render(BaseController.user, renderCollectionCreated(result.read())))
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
    views.html.core.correction.render(correctionResult, renderResult(correctionResult), user, null /*toolObject */)

  def renderResult(correctionResult: CompleteResult[R]): Html

  def correctPart(form: SolutionType, exercise: Option[E], part: String, user: User): Try[CompleteResult[R]]

}
