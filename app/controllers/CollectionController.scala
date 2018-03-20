package controllers

import java.nio.file.Files

import javax.inject.{Inject, Singleton}
import model.Enums.ExerciseState
import model._
import model.core.CoreConsts._
import model.core._
import model.toolMains.{CollectionToolMain, ToolList}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{of, single}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CollectionController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val repository: Repository)(implicit ec: ExecutionContext)
  extends AFixedExController(cc, dbcp) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils with ExerciseFormMappings {

  override type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = ToolList.getExCollToolMainOption(toolType)

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> of[ExerciseState]))

  private def takeSlice[T](collection: Seq[T], page: Int): Seq[T] = collection slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, collection.size))

  private def numOfPages(completeSize: Int) = (completeSize / STEP) + 2

  // Admin

  def adminIndex(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request => toolMain.statistics map (stats => Ok(views.html.admin.collExes.collectionAdminMain(admin, stats, toolMain)))
  }

  def adminImportCollections(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      readAll(toolMain.resourcesFolder / (toolMain.urlPart + ".yaml")) match {
        case Failure(error)       => Future(BadRequest(error.toString))
        case Success(fileContent) => toolMain.readAndSave(fileContent) map {
          readAndSaveResult => Ok(views.html.admin.collExes.collPreview(admin, readAndSaveResult, toolMain))
        }
      }
  }

  def adminExportCollections(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request => toolMain.yamlString map (content => Ok(views.html.admin.export.render(admin, content, toolMain)))
  }

  def adminExportCollectionsAsFile(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      val file = Files.createTempFile(s"export_${toolMain.urlPart}", ".yaml")

      toolMain.yamlString map (content => write(file, content)) map { _ =>
        Ok.sendPath(file, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => Files.delete(file))
      }
  }

  def adminChangeCollectionState(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>
        for (formError <- formWithErrors.errors)
          Logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")
        Future(BadRequest("There has been an error!"))
      }

      def onFormRead(toolMain: CollectionToolMain): ExerciseState => Future[Result] = { newState =>
        toolMain.updateCollectionState(id, newState) map {
          case true  => Ok(Json.obj("id" -> id, "newState" -> newState.name))
          case false => BadRequest(Json.obj("message" -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead(toolMain))
  }

  def adminCollectionsList(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCompleteColls map { allColls =>
        // FIXME: get rid of cast...
        Ok(views.html.admin.collExes.adminCollectionList(admin, allColls.map(_.wrapped).asInstanceOf[Seq[CompleteCollectionWrapper]], toolMain))
      }
  }

  def adminNewCollectionForm(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestCollectionId map { id =>
        val collection = toolMain.instantiateCollection(id + 1, ExerciseState.RESERVED)
        Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = true))
      }
  }

  def adminEditCollectionForm(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCompleteCollById(id) map { maybeCollection =>
        val collection = maybeCollection getOrElse toolMain.instantiateCollection(id, ExerciseState.RESERVED)
        Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = false))
      }
  }

  def adminEditCollection(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, _) =>
    implicit request =>
      // FIXME: implement: editing of collection!
      //      Future(Ok("TODO: Editing collection...!"))
      ???
  }


  def adminCreateCollection(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (_, _) =>
    implicit request =>
      // FIXME: implement: creation of collection!
      //      Future(Ok("TODO: Creating new collection...!"))
      ???
  }

  def adminDeleteCollection(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteCollection(id) map {
        case true  => Ok(Json.obj("id" -> id))
        case false => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
      }
  }

  def adminExercisesInCollection(tool: String, collId: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExesInColl(collId) map { exesInColl =>
        // FIXME: with collection?
        Ok(views.html.admin.collExes.adminCollExercisesOverview(admin, collId, exesInColl, toolMain))
      }
  }

  // User

  def index(tool: String): EssentialAction = collectionList(tool, page = 1)

  def collectionList(toolType: String, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteColls map { allColls =>
        val filteredColls = allColls filter (_.state == ExerciseState.APPROVED)

        // FIXME: remove cast ...
        val collsToDisplay = takeSlice(filteredColls, page) map (_.wrapped.asInstanceOf[CompleteCollectionWrapper])

        Ok(views.html.core.userCollectionsOverview(user, collsToDisplay, toolMain, page, filteredColls.size / STEP + 1))
      }
  }

  def collection(toolType: String, id: Int, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteCollById(id) map {
        case None       => Redirect(controllers.routes.CollectionController.index(toolMain.urlPart))
        case Some(coll) =>
          val exercises = coll.exercises.filter(_.state == ExerciseState.APPROVED)

          // FIXME: remove cast ...
          Ok(views.html.core.collection(user, coll, takeSlice(exercises, page), toolMain, page, numOfPages(exercises.size)))
      }
  }

  def exercise(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val values: Future[(Option[(toolMain.CollType, toolMain.CompExType)], Int)] = for {
        collection <- toolMain.futureCollById(collId)
        compEx <- toolMain.futureCompleteExById(collId, id)
        numOfExes <- toolMain.numOfExesInColl(collId)
      } yield ((collection zip compEx).headOption, numOfExes)


      values flatMap {
        case (None, _)                     => Future(BadRequest(s"There is no collection with id $collId!"))
        case (Some((coll, ex)), numOfExes) => toolMain.renderExercise(user, coll, ex, numOfExes) map (f => Ok(f))
      }
  }

  def correct(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.correctAbstract(user, collId, id, isLive = false) map {
        case Failure(error)  => BadRequest(toolMain.onSubmitCorrectionError(user, error))
        case Success(result) =>
          result match {
            case Right(jsValue) => Ok(jsValue)
            case Left(html)     => Ok(html)
          }
      }
  }

  def correctLive(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.correctAbstract(user, collId, id, isLive = true) map {
        case Failure(error)  => BadRequest(toolMain.onLiveCorrectionError(error))
        case Success(result) => result match {
          case Right(jsValue) => Ok(jsValue)
          case Left(html)     => Ok(html)
        }
      }
  }

  def newExerciseForm(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureHighestIdInCollection(collId) map { highestId =>
        val newEx = toolMain.instantiateExercise(collId, highestId + 1, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(user, newEx, isCreation = true))
      }
  }

  def editExerciseForm(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(collId, exId) map {
        case None              => Ok(toolMain.renderExerciseEditForm(user, toolMain.instantiateExercise(collId, exId, ExerciseState.RESERVED), isCreation = true))
        case Some(newExercise) => Ok(toolMain.renderExerciseEditForm(user, newExercise, isCreation = false))
      }
  }


  def editExercise(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val onFormError: Form[toolMain.CompExType] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(s"The form has had an error for key '${formError.key}': " + formError.message)

        // FIXME: return in form...
        Future(BadRequest("TODO!"))
      }

      val onFormRead: toolMain.CompExType => Future[Result] = { newExercise =>
        toolMain.futureSaveExercise(newExercise) map {
          case false =>
            // TODO: make view?
            BadRequest("Your exercise could not be saved...")
          case true  => Ok(views.html.admin.collExes.editCollectionExercisePreview(user, newExercise, toolMain))
        }
      }

      toolMain.readExerciseFromForm(collId).fold(onFormError, onFormRead)
  }

  def deleteExerciseInCollection(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(collId, exId) map {
        case false => BadRequest("TODO!")
        case true  => Ok(Json.obj("id" -> exId, "collId" -> collId))
      }
  }

}
