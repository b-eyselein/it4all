package controllers.coll

import better.files._
import controllers.Secured
import javax.inject.{Inject, Singleton}
import model.core.CoreConsts._
import model.core._
import model.toolMains.{CollectionToolMain, ToolList}
import model.{ExerciseCollection, ExerciseState}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.single
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CollectionAdminController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AToolAdminController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[CollectionAdminController])

  override protected type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  // Routes

  def adminCollection(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCollById(collId) flatMap {
        case None             => Future.successful(onNoSuchCollection(admin, toolMain, collId))
        case Some(collection) =>
          toolMain.futureExercisesInColl(collId) map { exercises =>
            Ok(views.html.admin.collExes.collectionAdmin(admin, collection, exercises, toolMain, toolList))
          }
      }
  }

  // Administrate Collections

  def adminImportCollections(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      readSaveAndPreview[ExerciseCollection](
        toolMain.readCollectionsFromYaml,
        toolMain.futureInsertAndDeleteOldCollection,
        readAndSaveResult => views.html.admin.collExes.readCollectionsPreview(admin, readAndSaveResult, toolMain, toolList)
      )
  }

  def adminChangeCollectionState(tool: String, id: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>
        for (formError <- formWithErrors.errors)
          logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")
        Future(BadRequest("There has been an error!"))
      }

      def onFormRead(toolMain: CollectionToolMain): ExerciseState => Future[Result] = { newState =>
        toolMain.updateCollectionState(id, newState) map {
          case true  => Ok(Json.obj(idName -> id, "newState" -> newState.entryName))
          case false => BadRequest(Json.obj(messageName -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead(toolMain))
  }


  def adminExportCollections(tool: String): EssentialAction = futureWithUserWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.yamlString map {
        exportedContent => Ok(views.html.admin.exportedCollection.render(admin, exportedContent, toolMain, toolList))
      }
  }

  def adminExportCollectionsAsFile(tool: String): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      val file = File.newTemporaryFile(s"export_${toolMain.urlPart}", ".yaml")

      toolMain.yamlString map (content => file.write(content)) map { _ =>
        Ok.sendPath(file.path, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => file.delete())
      }
  }

  // Creating, updating and removing collections

  def adminEditCollectionForm(tool: String, id: Int): EssentialAction = futureWithUserWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCollById(id) map { maybeCollection: Option[ExerciseCollection] =>
        val collection = maybeCollection.getOrElse(
          ExerciseCollection(id, title = "", admin.username, text = "", ExerciseState.RESERVED, shortName = "")
        )

        Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = false, toolList))
      }
  }

  def adminNewCollectionForm(tool: String): EssentialAction = futureWithUserWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestCollectionId map { id =>
        val collection = ExerciseCollection(id + 1, title = "", admin.username, text = "", ExerciseState.RESERVED, shortName = "")
        Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = true, toolList))
      }
  }

  def adminEditCollection(tool: String, id: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, _) =>
    implicit request =>
      // FIXME: implement: editing of collection!
      Future(Ok("TODO: Editing collection...!"))
  }

  def adminCreateCollection(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (_, _) =>
    implicit request =>
      // FIXME: implement: creation of collection!
      Future(Ok("TODO: Creating new collection...!"))
  }

  def adminDeleteCollection(tool: String, id: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteCollection(id) map {
        case true  => Ok(Json.obj(idName -> id))
        case false => NotFound(Json.obj(messageName -> s"Die Sammlung mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
      }
  }

  // Administrate exercises in collection

  def adminImportExercises(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      toolMain.futureCollById(collId) flatMap {
        case None             => Future.successful(onNoSuchCollection(user, toolMain, collId))
        case Some(collection) =>

          readSaveAndPreview[toolMain.ExType](
            toolMain.readExercisesFromYaml(collection),
            toolMain.futureInsertExercise(collId, _),
            readAndSaveResult => views.html.admin.collExes.readExercisesPreview(user, collection, readAndSaveResult, toolMain, toolList, toolMain.previewExerciseRest)
          )
      }
  }

  def adminChangeExerciseState(tool: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>
        for (formError <- formWithErrors.errors)
          logger.error(s"Form error while changinge state of exercise $exId: ${formError.message}")
        Future(BadRequest("There has been an error!"))
      }

      def onFormRead(toolMain: CollectionToolMain): ExerciseState => Future[Result] = { newState =>
        toolMain.updateExerciseState(collId, exId, newState) map {
          case true  => Ok(Json.obj(idName -> exId, "newState" -> newState.entryName))
          case false => BadRequest(Json.obj(messageName -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead(toolMain))
  }

  def adminExportExercises(toolType: String, collId: Int): EssentialAction = ???


  def adminExportExercisesAsFile(toolType: String, collId: Int): EssentialAction = ???

  // Creating, updating and removing exercises

  def adminEditExerciseForm(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCollById(collId) flatMap {
        case None             => Future.successful(onNoSuchCollection(admin, toolMain, collId))
        case Some(collection) =>
          toolMain.futureExerciseById(collId, id) map {
            case None           => onNoSuchExercise(admin, toolMain, collection, id)
            case Some(exercise) => Ok(toolMain.renderExerciseEditForm(admin, collId, exercise, isCreation = false, toolList))
          }
      }
  }

  def adminNewExerciseForm(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestExerciseIdInCollection(collId) map { id =>
        val exercise = toolMain.instantiateExercise(id + 1, admin.username, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(admin, collId, exercise, isCreation = true, toolList))
      }
  }

  def adminEditExercise(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>

      //      def onFormError: Form[toolMain.ExType] => Future[Result] = { formWithError =>
      //
      //        for (formError <- formWithError.errors)
      //          Logger.error(formError.key + " :: " + formError.message)
      //
      //        Future(BadRequest("Your form has has errors!"))
      //      }

      //      def onFormSuccess: toolMain.ExType => Future[Result] = { compEx =>
      // //        FIXME: save ex
      //        toolMain.futureUpdateExercise(compEx) map { _ =>
      //          Ok(views.html.admin.singleExercisePreview(admin, compEx, toolMain))
      //        }
      //      }

      //      toolMain.compExForm.bindFromRequest().fold(onFormError, onFormSuccess)
      ???
  }

  def adminCreateExercise(toolType: String, collId: Int): EssentialAction = ???


  def adminDeleteExercise(tool: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(collId, exId) map {
        case true  => Ok(Json.obj(idName -> exId))
        case false => NotFound(Json.obj(messageName -> s"Die Aufgabe mit ID $exId existiert nicht und kann daher nicht geloescht werden!"))
      }
  }

  // Reviews

  def exerciseReviewsList(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      ???
    //      toolMain.futureAllReviews map {
    //        allReviews => Ok(views.html.admin.idExes.idExerciseReviewsList(admin, allReviews, toolList, toolMain))
    //      }
  }

  def showReviews(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      //      toolMain.futureExerciseById(collId, id) flatMap {
      //        case None    => Future(onNoSuchExercise(id))
      //        case Some(_) => toolMain.futureReviewsForExercise(id) map {
      //          reviews => Ok(views.html.admin.idExes.idExerciseReviewListExercise(admin, reviews, toolList, toolMain))
      //        }
      //      }
      ???
  }


}
