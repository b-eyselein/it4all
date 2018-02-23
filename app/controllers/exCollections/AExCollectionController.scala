package controllers.exCollections

import java.nio.file.Files

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model._
import model.core.CoreConsts._
import model.core._
import net.jcazevedo.moultingyaml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object AExCollectionController {

  val CollectionToolMains: mutable.Map[String, AExCollectionToolMain[_ <: Exercise, _ <: CompleteEx[_], _, _]] = mutable.Map.empty

}

@Singleton
class AExCollectionController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val tables: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {


  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.username} - ${Json.toJson(eventToLog)}""")

  // Admin

  def adminIndex(tool: String): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      toolMain.statistics map (stats => Ok(views.html.admin.collectionAdminMain(user, stats, toolMain)))
  }

  def adminImportCollections(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      val file = toolMain.resourcesFolder / (toolMain.exType + ".yaml")

      val reads = readAll(file).get.parseYamls map toolMain.yamlFormat.read

      val (successes, failures) = CommonUtils.splitTries(reads)

//      successes.foreach(cc => println(cc.exercises.size))

      toolMain.saveRead(successes) map {
        _ => Ok(views.html.admin.collPreview(admin, successes map toolMain.wrap, failures, toolMain))
      }
  }

  def adminExportCollections(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      toolMain.futureCompleteColls map (colls => Ok(views.html.admin.export.render(admin, yamlString(colls), toolMain)))
  }

  def adminExportCollectionsAsFile(tool: String): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      val file = Files.createTempFile(s"export_${toolMain.exType}", ".yaml")
      toolMain.futureCompleteColls map (exes => {

        write(file, yamlString(exes))

        Ok.sendPath(file, fileName = _ => s"export_${toolMain.exType}.yaml", onClose = () => Files.delete(file))
      })
  }

  def adminChangeCollectionState(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
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

  def adminCollectionsList(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      toolMain.futureCompleteColls map (colls => Ok(views.html.admin.collectionList(admin, colls map toolMain.wrap)))
  }

  def adminEditCollectionForm(tool: String, id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      toolMain.futureCompleteCollById(id) map (coll => Ok(collEditForm(admin, coll)))
  }

  def adminEditCollection(tool: String, id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      // FIXME: implement: editing of collection!
      Ok("TODO: Editing collection...!")
  }

  def adminNewCollectionForm(tool: String): EssentialAction = withAdmin { admin =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      Ok(collEditForm(admin, None))
  }

  def adminCreateCollection(tool: String): EssentialAction = withAdmin { _ =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      // FIXME: implement: creation of collection!
      Ok("TODO: Creating new collection...!")
  }

  private def collEditForm(admin: User, collection: Option[CompColl]): Html =
    views.html.admin.collectionEditForm(admin, collection map toolMain.wrap, adminRenderEditRest(collection))

  def adminDeleteCollection(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      tables.deleteColl(id) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case _ => Ok(Json.obj("id" -> id))
      }
  }

  // Views and other helper methods for admin

  // TODO: scalarStyle = Folded if fixed...
  private def yamlString(exes: Seq[CompColl]): String = "%YAML 1.2\n---\n" + (exes map (_.toYaml.print(Auto /*, Folded*/)) mkString "---\n")

  // User

  def index(tool: String): EssentialAction = collectionList(tool, page = 1)

  def collectionList(tool: String, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      toolMain.futureCompleteColls map (allColls => Ok(views.html.core.collsList(user, takeSlice(allColls, page) map wrap, renderExesListRest, toolMain, allColls.size / STEP + 1)))
  }

  def collection(tool: String, id: Int, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      toolMain.futureCompleteCollById(id) map {
        case None       => Redirect(toolMain.indexCall)
        case Some(coll) => Ok(views.html.core.collection(user, wrap(coll), takeSlice(coll.exercises, page), toolMain, page, numOfPages(coll.exercises.size)))
      }
  }

  def filteredCollection(tool: String, id: Int, filter: String, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      toolMain.futureCompleteCollById(id) map {
        case None       => Redirect(controllers.exes.idPartExes.routes.AExCollectionController.index(toolMain.urlPart))
        case Some(coll) =>
          val filteredExes = coll exercisesWithFilter filter
          Ok(views.html.core.filteredCollection(user, wrap(coll), takeSlice(filteredExes, page), toolMain, filter, page, numOfPages(filteredExes.size)))
      }
  }

  // User

  def exercise(tool: String, collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      (toolMain.futureCollById(collId) zip toolMain.futureCompleteExById(collId, id) zip toolMain.numOfExesInColl(collId)) map {
        optTuple => ((optTuple._1._1 zip optTuple._1._2).headOption, optTuple._2)
      } flatMap {
        case (None, _)                     => Future(BadRequest("TODO!"))
        case (Some((coll, ex)), numOfExes) => renderExercise(user, coll, ex, numOfExes) map (f => Ok(f))
      }
  }


  def correct(tool: String, collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      correctAbstract(user, collId, id, toolMain.readSolutionFromPostRequest, toolMain.onSubmitCorrectionResult(user, _), toolMain.onSubmitCorrectionError(user, _))
  }

  def correctLive(tool: String, collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = AExCollectionController.CollectionToolMains(tool)
      correctAbstract(user, collId, id, toolMain.readSolutionFromPutRequest, toolMain.onLiveCorrectionResult, toolMain.onLiveCorrectionError)
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
