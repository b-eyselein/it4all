package controllers.exCollections

import java.nio.file.Files

import controllers.Secured
import model._
import model.core.CoreConsts._
import model.core._
import model.core.tools.CollectionToolObject
import model.yaml.MyYamlFormat
import net.jcazevedo.moultingyaml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


abstract class AExCollectionController[Ex <: ExerciseInCollection, CompEx <: CompleteEx[Ex], Coll <: ExerciseCollection[Ex, CompEx], CompColl <: CompleteCollection[Ex, CompEx, Coll],
R <: EvaluationResult, CompResult <: CompleteResult[R], Tables <: ExerciseCollectionTableDefs[Ex, CompEx, Coll, CompColl]]
(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val tables: Tables, val toolObject: CollectionToolObject)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

  // Reading solution from Request

  type SolType

  def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[SolType]

  // Reading Yaml

  implicit val yamlFormat: MyYamlFormat[CompColl]

  // Database queries

  protected def numOfExes: Future[Int] = tables.futureNumOfExes

  protected def numOfExesInColl(id: Int): Future[Int]

  protected def futureCollById(id: Int): Future[Option[Coll]] = tables.futureCollById(id)

  protected def futureCompleteColls: Future[Seq[CompColl]] = tables.futureCompleteColls

  protected def futureCompleteCollById(id: Int): Future[Option[CompColl]] = tables.futureCompleteCollById(id)

  protected def futureCompleteExById(collId: Int, id: Int): Future[Option[CompEx]] = tables.futureCompleteExById(collId, id)

  protected def statistics: Future[Html] = numOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  protected def saveRead(read: Seq[CompColl]): Future[Seq[Boolean]]

  protected def wrap(compColl: CompColl): CompleteCollectionWrapper

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.username} - ${Json.toJson(eventToLog)}""")

  // Admin

  def adminIndex: EssentialAction = futureWithAdmin { user =>
    implicit request => statistics map (stats => Ok(views.html.admin.collectionAdminMain(user, stats, toolObject)))
  }

  def adminImportCollections: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = toolObject.resourcesFolder / (toolObject.exType + ".yaml")

      val reads: Seq[Try[CompColl]] = readAll(file).get.parseYamls map yamlFormat.read

      val (successes, failures) = CommonUtils.splitTries(reads)

      successes.foreach(cc => println(cc.exercises.size))

      saveRead(successes) map {
        _ => Ok(views.html.admin.collPreview(admin, successes map wrap, failures, toolObject))
      }
  }

  def adminExportCollections: EssentialAction = futureWithAdmin { admin =>
    implicit request => futureCompleteColls map (colls => Ok(views.html.admin.export.render(admin, yamlString(colls), toolObject)))
  }

  def adminExportCollectionsAsFile: EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      val file = Files.createTempFile(s"export_${toolObject.exType}", ".yaml")
      futureCompleteColls map (exes => {

        write(file, yamlString(exes))

        Ok.sendPath(file, fileName = _ => s"export_${toolObject.exType}.yaml", onClose = () => Files.delete(file))
      })
  }

  def adminChangeCollectionState(id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      //      import play.api.data.Forms._
      //      case class StrForm(str: String)
      //      val form = Form(mapping("state" -> nonEmptyText)(StrForm.apply)(StrForm.unapply))
      //
      //      val newState: ExerciseState = Try(ExerciseState.valueOf(form.bindFromRequest.get.str)) getOrElse ExerciseState.RESERVED
      //
      //      val updateAction = (for {coll <- tq if coll.id === id} yield coll.state).update(newState)
      //      db.run(updateAction).map {
      //        case 1 => Ok(Json.obj("id" -> id, "newState" -> newState.name))
      //        case _ => BadRequest(Json.obj("message" -> "No such file exists..."))
      //      }
      Future(BadRequest("Not yet implemented: changing the state of a collection"))
  }

  def adminCollectionsList: EssentialAction = futureWithAdmin { admin =>
    implicit request => futureCompleteColls map (colls => Ok(views.html.admin.collectionList(admin, colls map wrap, toolObject)))
  }

  def adminEditCollectionForm(id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request => futureCompleteCollById(id) map (coll => Ok(collEditForm(admin, coll)))
  }

  def adminEditCollection(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      // FIXME: implement: editing of collection!
      Ok("TODO: Editing collection...!")
  }

  def adminNewCollectionForm: EssentialAction = withAdmin { admin =>
    implicit request => Ok(collEditForm(admin, None))
  }

  def adminCreateCollection: EssentialAction = withAdmin { _ =>
    implicit request =>
      // FIXME: implement: creation of collection!
      Ok("TODO: Creating new collection...!")
  }

  private def collEditForm(admin: User, collection: Option[CompColl]): Html =
    views.html.admin.collectionEditForm(admin, toolObject, collection map wrap, adminRenderEditRest(collection))

  def adminDeleteCollection(id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      tables.deleteColl(id) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case _ => Ok(Json.obj("id" -> id))
      }
  }

  // Views and other helper methods for admin

  // TODO: scalarStyle = Folded if fixed...
  private def yamlString(exes: Seq[CompColl]): String = "%YAML 1.2\n---\n" + (exes map (_.toYaml.print(Auto /*, Folded*/)) mkString "---\n")

  // User

  def index: EssentialAction = collectionList(page = 1)

  def collectionList(page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteColls map (allColls => Ok(views.html.core.collsList(user, takeSlice(allColls, page) map wrap, renderExesListRest, toolObject, allColls.size / STEP + 1)))
  }

  def collection(id: Int, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteCollById(id) map {
        case None       => Redirect(toolObject.indexCall)
        case Some(coll) => Ok(views.html.core.collection(user, wrap(coll), takeSlice(coll.exercises, page), toolObject, page, numOfPages(coll.exercises.size)))
      }
  }

  def filteredCollection(id: Int, filter: String, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteCollById(id) map {
        case None       => Redirect(toolObject.indexCall)
        case Some(coll) =>
          val filteredExes = coll exercisesWithFilter filter
          Ok(views.html.core.filteredCollection(user, wrap(coll), takeSlice(filteredExes, page), toolObject, filter, page, numOfPages(filteredExes.size)))
      }
  }

  // User

  def exercise(collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      (futureCollById(collId) zip futureCompleteExById(collId, id) zip numOfExesInColl(collId)) map {
        optTuple => ((optTuple._1._1 zip optTuple._1._2).headOption, optTuple._2)
      } flatMap {
        case (None, _)                     => Future(BadRequest("TODO!"))
        case (Some((coll, ex)), numOfExes) => renderExercise(user, coll, ex, numOfExes) map (f => Ok(f))
      }
  }


  def correct(collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, collId, id, readSolutionFromPostRequest, onSubmitCorrectionResult(user, _), onSubmitCorrectionError(user, _))
  }

  def correctLive(collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, collId, id, readSolutionFromPutRequest, onLiveCorrectionResult, onLiveCorrectionError)
  }

  private def correctAbstract[S, Err](user: User, collId: Int, id: Int, maybeSolution: Option[SolType], onCorrectionSuccess: CompResult => Result,
                                      onCorrectionError: Throwable => Result)(implicit request: Request[AnyContent]): Future[Result] =
    maybeSolution match {
      case None           => Future(onCorrectionError(SolutionTransferException))
      case Some(solution) => (futureCompleteExById(collId, id) zip futureCollById(collId)) map (opts => (opts._1 zip opts._2).headOption) flatMap {
        case None                         => Future(onCorrectionError(NoSuchExerciseException(id)))
        case Some((exercise, collection)) => correctEx(user, solution, exercise, collection) map {
          case Success(result) => onCorrectionSuccess(result)
          case Failure(error)  => onCorrectionError(error)
        }
      }
    }

  // Correction

  protected def correctEx(user: User, form: SolType, exercise: CompEx, collection: Coll): Future[Try[CompResult]]

  // Views

  protected def renderExercise(user: User, coll: Coll, exercise: CompEx, numOfExes: Int): Future[Html]

  protected def onSubmitCorrectionResult(user: User, result: CompResult): Result

  protected def onSubmitCorrectionError(user: User, error: Throwable): Result

  protected def onLiveCorrectionResult(result: CompResult): Result

  protected def onLiveCorrectionError(error: Throwable): Result

  protected def adminRenderEditRest(exercise: Option[CompColl]): Html

  /**
    * Used for rendering things such as playgrounds
    *
    * @return Html - with link to other "exercises"
    */
  protected def renderExesListRest: Html = new Html("")

  // Other helper methods

  private def takeSlice[T](collection: Seq[T], page: Int): Seq[T] =
    collection slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, collection.size))

  private def numOfPages(completeSize: Int) = (completeSize / STEP) + 2

}
