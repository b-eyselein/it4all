package controllers.exes

import java.nio.file.Files

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model.Enums.ExerciseState
import model._
import model.core.CoreConsts._
import model.core._
import model.toolMains.ToolList
import play.api.Logger
import play.api.data.Forms.{of, single}
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CollectionController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

  // Helpers

  private implicit object ExerciseStateFormatter extends Formatter[ExerciseState] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ExerciseState] = data.get(key) match {
      case None           => Left(Seq(FormError(key, "No value found!")))
      case Some(valueStr) => ExerciseState.byString(valueStr) match {
        case Some(state) => Right(state)
        case None        => Left(Seq(FormError(key, s"Value '$valueStr' is no legal value!")))
      }
    }

    override def unbind(key: String, value: ExerciseState): Map[String, String] = Map(key -> value.name)

  }

  val stateForm: Form[ExerciseState] = Form(single("state" -> of[ExerciseState]))

  // Admin

  def adminIndex(tool: String): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) => toolMain.statistics map (stats => Ok(views.html.admin.collectionAdminMain(user, stats, toolMain)))
      }
  }

  def adminImportCollections(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
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
      ToolList.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          toolMain.yamlString map (content => Ok(views.html.admin.export.render(admin, content, toolMain)))
      }
  }

  def adminExportCollectionsAsFile(tool: String): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          val file = Files.createTempFile(s"export_${toolMain.urlPart}", ".yaml")

          toolMain.yamlString map (content => write(file, content)) map { _ =>
            Ok.sendPath(file, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => Files.delete(file))
          }
      }
  }

  def adminChangeCollectionState(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")

        Future(BadRequest("There has been an error!"))
      }

      val onFormRead: ExerciseState => Future[Result] = { newState =>
        ToolList.getExCollToolMainOption(tool) match {
          case None           => Future(BadRequest(s"There is no such tool >>$tool<<"))
          case Some(toolMain) => toolMain.updateExerciseState(id, newState) map {
            case true  => Ok(Json.obj("id" -> id, "newState" -> newState.name))
            case false => BadRequest(Json.obj("message" -> "Could not update exercise!"))
          }
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead)
  }

  def adminCollectionsList(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) =>
          // FIXME: get rid of cast...
          toolMain.futureCompleteColls map (colls => Ok(views.html.admin.collectionList(admin, colls.map(_.wrapped).asInstanceOf[Seq[CompleteCollectionWrapper]], toolMain)))
      }
  }

  def adminNewCollectionForm(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) => toolMain.futureHighestCollectionId map { id =>
          val collection = toolMain.instantiateCollection(id + 1, ExerciseState.RESERVED)
          Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = true))
        }
      }
  }

  def adminEditCollectionForm(tool: String, id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
        case None           => Future(BadRequest(s"There is no tool with name >>$tool<<"))
        case Some(toolMain) => toolMain.futureCompleteCollById(id) map { maybeCollection =>
          val collection = maybeCollection getOrElse toolMain.instantiateCollection(id, ExerciseState.RESERVED)
          Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = false))
        }
      }
  }

  def adminEditCollection(tool: String, id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
        case None    => BadRequest(s"There is no tool with name >>$tool<<")
        case Some(_) =>
          // FIXME: implement: editing of collection!
          Ok("TODO: Editing collection...!")
      }
  }

  def adminCreateCollection(tool: String): EssentialAction = withAdmin { _ =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
        case None    => BadRequest(s"There is no tool with name >>$tool<<")
        case Some(_) =>
          // FIXME: implement: creation of collection!
          Ok("TODO: Creating new collection...!")
      }
  }

  def adminDeleteCollection(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
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
      ToolList.getExCollToolMainOption(tool) match {
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
      ToolList.getExCollToolMainOption(tool) match {
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

  def exercise(tool: String, collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      ToolList.getExCollToolMainOption(tool) match {
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
      ToolList.getExCollToolMainOption(tool) match {
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
      ToolList.getExCollToolMainOption(tool) match {
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
