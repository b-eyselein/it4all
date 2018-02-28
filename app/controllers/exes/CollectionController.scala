package controllers.exes

import java.nio.file.Files

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model._
import model.core.CoreConsts._
import model.core._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CollectionController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val tables: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {


  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.username} - ${Json.toJson(eventToLog)}""")

  // Admin

  def adminIndex(tool: String): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) => toolMain.statistics map (stats => Ok(views.html.admin.collectionAdminMain(user, stats, toolMain)))
      }
  }

  def adminImportCollections(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          readAll(toolMain.resourcesFolder / (toolMain.urlPart + ".yaml")) match {
            case Failure(error)       => Future(BadRequest(error.toString))
            case Success(fileContent) =>
              toolMain.readAndSave(fileContent) map (readAndSaveResult => Ok(views.html.admin.collPreview(admin, readAndSaveResult, toolMain)))
          }
      }
  }

  def adminExportCollections(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.yamlString map (content => Ok(views.html.admin.export.render(admin, content, toolMain)))
      }
  }

  def adminExportCollectionsAsFile(tool: String): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          val file = Files.createTempFile(s"export_${toolMain.urlPart}", ".yaml")
          toolMain.futureCompleteColls map (exes => {

            toolMain.yamlString map (content => write(file, content))

            Ok.sendPath(file, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => Files.delete(file))
          })
      }
  }

  def adminChangeCollectionState(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
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
  }

  def adminCollectionsList(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.futureCompleteColls map (colls => Ok(views.html.admin.collectionList(admin, Seq.empty /* colls map toolMain.wrap*/ , toolMain)))
      }
  }

  def adminEditCollectionForm(tool: String, id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.futureCompleteCollById(id) map (maybeColl => Ok(collEditForm(admin, toolMain, None /*maybeColl map (toolMain.wrap(_)*/)))
      }
  }

  def adminEditCollection(tool: String, id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None    => BadRequest(s"There is no tool with name >>$tool<<")
        case Some(_) =>
          // FIXME: implement: editing of collection!
          Ok("TODO: Editing collection...!")
      }
  }

  def adminNewCollectionForm(tool: String): EssentialAction = withAdmin { admin =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => BadRequest(s"There is no tool with name >>$tool<<")
        case Some(toolMain) => Ok(collEditForm(admin, toolMain, None))
      }
  }

  def adminCreateCollection(tool: String): EssentialAction = withAdmin { _ =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None    => BadRequest(s"There is no tool with name >>$tool<<")
        case Some(_) =>
          // FIXME: implement: creation of collection!
          Ok("TODO: Creating new collection...!")
      }
  }

  private def collEditForm(admin: User, toolMain: AExCollectionToolMain, collection: Option[_ <: CompleteCollectionWrapper]): Html =
    views.html.admin.collectionEditForm(admin, toolMain, collection, new Html("") /*adminRenderEditRest(collection)*/)

  def adminDeleteCollection(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None    => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(_) =>
          Future(BadRequest("TODO!"))
        //      tables.deleteColl(id) map {
        //        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        //        case _ => Ok(Json.obj("id" -> id))
        //      }
      }
  }

  // User

  def index(tool: String): EssentialAction = collectionList(tool, page = 1)

  def collectionList(tool: String, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.futureCompleteColls map {
            allColls =>
              // FIXME: remove cast ...
              Ok(views.html.core.collsList(user, takeSlice(allColls, page) map (_.wrapped.asInstanceOf[CompleteCollectionWrapper]),
                renderExesListRest, toolMain, allColls.size / STEP + 1))
          }
      }
  }

  def collection(tool: String, id: Int, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.futureCompleteCollById(id) map {
            case None       => Redirect(routes.CollectionController.index(toolMain.urlPart))
            case Some(coll) =>
              // FIXME: remove cast ...
              Ok(views.html.core.collection(user, coll.wrapped.asInstanceOf[CompleteCollectionWrapper],
                takeSlice(coll.exercises, page), toolMain, page, numOfPages(coll.exercises.size)))
          }
      }
  }

  // User

  def exercise(tool: String, collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          (toolMain.futureCollById(collId) zip toolMain.futureCompleteExById(collId, id) zip toolMain.numOfExesInColl(collId)) map {
            optTuple => ((optTuple._1._1 zip optTuple._1._2).headOption, optTuple._2)
          } flatMap {
            case (None, _)                     => Future(BadRequest("TODO!"))
            case (Some((coll, ex)), numOfExes) => toolMain.renderExercise(user, coll, ex, numOfExes) map (f => Ok(f))
          }
      }
  }

  def correct(tool: String, collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.correctAbstract(user, collId, id, isLive = false) map {
            case Failure(error)  => BadRequest(toolMain.onSubmitCorrectionError(user, error))
            case Success(result) =>
              result match {
                case Right(jsValue) => Ok(jsValue)
                case Left(html)     => Ok(html)
              }
          }
      }
  }

  def correctLive(tool: String, collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      SingleExerciseController.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.correctAbstract(user, collId, id, isLive = true) map {
            case Failure(error)  => BadRequest(toolMain.onLiveCorrectionError(error))
            case Success(result) => result match {
              case Right(jsValue) => Ok(jsValue)
              case Left(html)     => Ok(html)
            }
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
