package controllers.exCollections

import java.nio.file.Files

import controllers.Secured
import model._
import model.core.CoreConsts._
import model.core._
import net.jcazevedo.moultingyaml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.http.Writeable
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait CollectionToolObject extends model.core.tools.ExToolObject with FileUtils {

  val collectionName: String

  def exerciseRoute(exercise: CompEx): Call

  def collectionRoute(id: Int, page: Int = 1): Call

  def filteredCollectionRoute(id: Int, filter: String, page: Int = 1): Call

  def correctLiveRoute(exercise: HasBaseValues): Call

  def correctRoute(exercise: HasBaseValues): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = Map(exerciseRoute(exercise) -> "Aufgabe bearbeiten")

}

abstract class AExCollectionController[E <: Exercise, C <: ExerciseCollection[E, _], R <: EvaluationResult]
(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository, val toolObject: CollectionToolObject)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

  // Reading solution from Request

  type SolType

  def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[SolType]

  // Reading Yaml

  type CompColl <: CompleteCollection

  type CompEx <: CompleteEx[E]

  implicit val yamlFormat: YamlFormat[CompColl]

  // Database queries

  import profile.api._

  protected type TQ <: repo.HasBaseValuesTable[C]

  protected def tq: TableQuery[TQ]

  protected def numOfExes: Future[Int] = db.run(tq.size.result)

  protected def completeColls: Future[Seq[CompColl]]

  protected def collById(id: Int): Future[Option[C]] = db.run(tq.filter(_.id === id).result.headOption)

  protected def completeCollById(id: Int): Future[Option[CompColl]] = ???

  protected def futureCompleteExById(collId: Int, id: Int): Future[Option[CompEx]] = ???

  protected def statistics: Future[Html] = numOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  protected def saveRead(read: Seq[CompColl]): Future[Seq[Boolean]]

  // Reading from form TODO: Move to other file?

  //  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.username} - ${Json.toJson(eventToLog)}""")

  // Admin

  def adminIndex: EssentialAction = futureWithAdmin { user =>
    implicit request => statistics map (stats => Ok(views.html.admin.adminMain.render(user, stats, toolObject, new Html(""))))
  }

  def adminImportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = toolObject.resourcesFolder / (toolObject.exType + ".yaml")
      val read = readAll(file).get.parseYamls map (_.convertTo[CompColl])
      saveAndPreviewExercises(admin, read)
  }

  /**
    * FIXME: save and import in same method? => in direct subclasses!?!
    *
    * @param admin current user, has to be admin
    * @param read  Seq of read (exercises)
    * @return
    */
  protected def saveAndPreviewExercises(admin: User, read: Seq[CompColl]): Future[Result] =
    saveRead(read) map (_ => Ok(views.html.admin.collPreview(admin, read, toolObject))) recover {
      // FIXME: Failures!
      case throwable: Throwable =>
        throwable.printStackTrace()
        BadRequest(throwable.getMessage)
    }

  def adminExportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request => completeColls map (exes => Ok(views.html.admin.export.render(admin, yamlString(exes), toolObject)))
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
    // TODO
    implicit request => completeCollById(id) map (ex => Ok("TODO!" /*views.html.admin.editExForm(admin, toolObject, ex, renderEditRest(ex))*/))
  }

  def adminExerciseList: EssentialAction = futureWithAdmin { admin =>
    implicit request => completeColls map (colls => Ok(views.html.admin.collectionList.render(admin, colls, toolObject)))
  }

  def adminNewExerciseForm: EssentialAction = withAdmin { admin =>
    // FIXME
    implicit request =>
      // FIXME: ID of new exercise?
      Ok(views.html.admin.editExForm.render(admin, toolObject, None, renderEditRest(None)))
  }

  def newExercise: EssentialAction = withAdmin { admin =>
    implicit request => ???
    //      Ok("TODO: Not yet used...")
  }

  // Views and other helper methods for admin

  // FIXME: scalarStyle = Folded if fixed...
  private def yamlString(exes: Seq[CompColl]): String = "%YAML 1.2\n---\n" + (exes map (_.toYaml.print(Auto /*, Folded*/)) mkString "---\n")

  // User

  private def takeSlice[T](collection: Seq[T], page: Int): Seq[T] =
    collection slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, collection.size))

  def index: EssentialAction = collectionList(page = 1)

  def collectionList(page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeColls map (allColls => Ok(views.html.core.collsList(user, takeSlice(allColls, page), renderExesListRest, toolObject, allColls.size / STEP + 1)))
  }

  def collection(id: Int, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeCollById(id) map {
        case Some(coll) =>
          val exes = takeSlice(coll.exercises, page)
          Ok(views.html.core.collection(user, coll, exes, toolObject, page, (coll.exercises.size / STEP) + 2))
        case None       => BadRequest("TODO!")
      }
  }

  def filteredCollection(id: Int, filter: String, page: Int): EssentialAction = futureWithUser { user =>
    implicit request => completeCollById(id) map (coll => Ok("TODO!"))
    //      Option(SqlScenario.finder.byId(id)) match {
    //        case None => Redirect(controllers.sql.routes.SqlController.index())
    //        case Some(scenario) =>
    //          if (site <= 0)
    //            Redirect(controllers.sql.routes.SqlController.filteredScenario(id, exType))
    //          else {
    //            val start = SqlScenario.STEP * (site - 1)
    //
    //            val allByType = scenario.getExercisesByType(SqlExerciseType.valueOf(exType))
    //            val exercises = allByType.subList(start, Math.min(start + SqlScenario.STEP, allByType.size))
    //
    //            Ok(views.html.sqlScenario.render(user, exercises.asScala.toList, scenario, SqlExerciseType.valueOf(exType), site))
    //          }
    //      }
  }

  /**
    * Used for rendering things such as playgrounds
    *
    * @return Html - with link to other "exercises"
    */
  protected def renderExesListRest: Html = new Html("")

  // Helper methods

  def renderEditRest(exercise: Option[CompColl]): Html = ???

  def renderExercises(exercises: List[CompColl]): Html = ??? //views.html.admin.exercisesTable(exercises, null /*toolObject */)


  // Admin

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
      //  Ok(views.html.admin.exPreview(BaseController.user,
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

  def renderCollectionCreated(collections: List[SingleReadingResult[C]]): Html

  def renderExEditForm(user: User, exercise: C, isCreation: Boolean): Html

  def renderExCollCreationForm(user: User, collection: C): Html

  def renderExerciseCollections(user: User, allCollections: List[C]): Html

  // User

  def exercise(collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      (collById(collId) zip futureCompleteExById(collId, id)) map { optTuple => (optTuple._1 zip optTuple._2).headOption } map {
        case None             => BadRequest("TODO!")
        case Some((coll, ex)) => Ok(renderExercise(user, coll, ex))
      }
  }

  protected def renderExercise(user: User, coll: C, exercise: CompEx): Html

  def correct(collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      correctAbstract(user, collId, id, readSolutionFromPostRequest, renderCorrectionResult(user, _),
        //FIXME: on error!
        error => views.html.main("Fehler", user, new Html(""))(new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")))
  }

  def correctLive(collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, collId, id, readSolutionFromPutRequest, renderResult, error => {error.printStackTrace(); Json.obj("message" -> error.getMessage)})
  }

  private def correctAbstract[S, Err](user: User, collId: Int, id: Int, maybeSolution: Option[SolType],
                                      onCorrectionSuccess: CompleteResult[R] => S, onCorrectionError: Throwable => Err)
                                     (implicit successWriteable: Writeable[S], errorWriteable: Writeable[Err], request: Request[AnyContent]): Future[Result] =
    maybeSolution match {
      case None           => Future(BadRequest("No solution!"))
      case Some(solution) => (futureCompleteExById(collId, id) zip collById(collId)) map (opts => (opts._1 zip opts._2).headOption) map {
        case None                         => NotFound("No such exercise!")
        case Some((exercise, collection)) => correctEx(solution, exercise, collection, user) match {
          case Success(result) => Ok(onCorrectionSuccess(result))
          case Failure(error)  => BadRequest(onCorrectionError(error))
        }
      }
    }

  private def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction(correctionResult, renderResult(correctionResult), user, toolObject)

  protected def renderResult(correctionResult: CompleteResult[R]): Html

  protected def correctEx(form: SolType, exercise: CompEx, collection: C, user: User): Try[CompleteResult[R]]

}
