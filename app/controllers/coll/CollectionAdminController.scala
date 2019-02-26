package controllers.coll

import better.files._
import controllers.{AFixedExController, Secured}
import javax.inject.{Inject, Singleton}
import model.ExerciseState
import model.core.CoreConsts._
import model.core._
import model.toolMains.{CollectionToolMain, ToolList}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.single
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class CollectionAdminController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AFixedExController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[CollectionAdminController])

  override protected type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  override protected val adminRightsRequired: Boolean = true

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  // Admin

  def adminCollection(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCollById(collId) flatMap {
        case None             => ???
        case Some(collection) =>
          toolMain.futureExercisesInColl(collId) map {
            exercises => Ok(views.html.admin.collExes.collectionAdmin(user, collection, exercises, toolMain, toolList))
          }
      }
  }

  def adminImportCollections(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      // FIXME: refactor!!!!!!!!!

      val readTries: Seq[Try[toolMain.CollType]] = toolMain.readCollectionsFromYaml

      val (readSuccesses, readFailures) = CommonUtils.splitTriesNew(readTries)

      val readAndSaveSuccesses: Future[Seq[(toolMain.CollType, Boolean)]] = Future.sequence(readSuccesses.map { readCollection =>
        toolMain.futureInsertAndDeleteOldCollection(readCollection) map (saved => (readCollection, saved))
      })

      readAndSaveSuccesses.map { saveResults: Seq[(toolMain.CollType, Boolean)] =>
        val readAndSaveResult = ReadAndSaveResult(saveResults map {
          sr => new ReadAndSaveSuccess[toolMain.CollType](sr._1, sr._2)
        }, readFailures)

        for (failure <- readAndSaveResult.failures) {
          logger.error("There has been an error reading a yaml object: ", failure.exception)
        }

        Ok(toolMain.previewCollectionReadAndSaveResult(admin, readAndSaveResult, toolList))
      }
  }

  def adminImportExercises(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      // FIXME: refactor!!!!!!!!!

      toolMain.futureCollById(collId) flatMap {
        case None             => ???
        case Some(collection) =>
          val readTries: Seq[Try[toolMain.ExType]] = toolMain.readExercisesFromYaml(collection)

          val (readSuccesses, readFailures) = CommonUtils.splitTriesNew(readTries)

          val readAndSaveSuccesses: Future[Seq[(toolMain.ExType, Boolean)]] = Future.sequence(readSuccesses.map {
            readExercise => toolMain.futureInsertExercise(readExercise) map (saved => (readExercise, saved))
          })

          readAndSaveSuccesses.map { saveResults: Seq[(toolMain.ExType, Boolean)] =>
            val readAndSaveResult = ReadAndSaveResult(saveResults map {
              sr => new ReadAndSaveSuccess[toolMain.ExType](sr._1, sr._2)
            }, readFailures)

            for (failure <- readAndSaveResult.failures) {
              logger.error("There has been an error reading an yaml object", failure.exception)
            }

            Ok(toolMain.previewExerciseReadsAndSaveResult(user, readAndSaveResult, toolList))
          }

      }
  }

  def adminExportExercises(toolType: String, collId: Int): EssentialAction = ???

  def adminExportCollections(tool: String): EssentialAction = futureWithUserWithToolMain(tool) { (admin, toolMain) =>
    implicit request => toolMain.yamlString map (content => Ok(views.html.admin.export.render(admin, content, toolMain, toolList)))
  }

  def adminExportCollectionsAsFile(tool: String): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      val file = File.newTemporaryFile(s"export_${toolMain.urlPart}", ".yaml")

      toolMain.yamlString map (content => file.write(content)) map { _ =>
        Ok.sendPath(file.path, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => file.delete())
      }
  }

  def adminChangeCollectionState(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
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

  def adminChangeExerciseState(tool: String, collId: Int, exId: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
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

  def adminCollectionsList(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureAllCollections map { allColls =>
        Ok(views.html.admin.collExes.adminCollectionList(admin, allColls, toolMain, toolList))
      }
  }

  def adminNewCollectionForm(tool: String): EssentialAction = futureWithUserWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestCollectionId map { id =>
        val collection: toolMain.CollType = toolMain.instantiateCollection(id + 1, admin.username, ExerciseState.RESERVED)
        Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = true, toolList))
      }
  }

  def adminEditCollectionForm(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCollById(id) map { maybeCollection: Option[toolMain.CollType] =>
        val collection: toolMain.CollType = maybeCollection getOrElse toolMain.instantiateCollection(id, admin.username, ExerciseState.RESERVED)
        Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = false, toolList))
      }
  }

  def adminEditCollection(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, _) =>
    implicit request =>
      // FIXME: implement: editing of collection!
      Future(Ok("TODO: Editing collection...!"))
  }

  def adminCreateCollection(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (_, _) =>
    implicit request =>
      // FIXME: implement: creation of collection!
      Future(Ok("TODO: Creating new collection...!"))
  }

  def adminDeleteCollection(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteCollection(id) map {
        case true  => Ok(Json.obj(idName -> id))
        case false => NotFound(Json.obj(messageName -> s"Die Sammlung mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
      }
  }

  def adminDeleteExercise(tool: String, collId: Int, exId: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(collId, exId) map {
        case true  => Ok(Json.obj(idName -> exId))
        case false => NotFound(Json.obj(messageName -> s"Die Aufgabe mit ID $exId existiert nicht und kann daher nicht geloescht werden!"))
      }
  }

  def adminExercisesInCollection(tool: String, collId: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureExercisesInColl(collId) map { exesInColl =>
        // FIXME: with collection?
        Ok(views.html.admin.collExes.adminCollExercisesOverview(admin, collId, exesInColl, toolMain, toolList))
      }
  }

}
