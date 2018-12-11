package controllers

import better.files._
import javax.inject.{Inject, Singleton}
import model.ExerciseState
import model.core.CoreConsts._
import model.core._
import model.toolMains.{CollectionToolMain, ToolList}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.single
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, JsString, Json}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CollectionController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AFixedExController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  override type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  private def takeSlice[T](collection: Seq[T], page: Int, step: Int = stdStep): Seq[T] = {
    val start = Math.max(0, (page - 1) * step)
    val end = Math.min(page * step, collection.size)

    collection slice(start, end)
  }

  // Admin

  def adminExportCollections(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request => toolMain.yamlString map (content => Ok(views.html.admin.export.render(admin, content, toolMain, toolList)))
  }

  def adminExportCollectionsAsFile(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (_, toolMain) =>
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
          Logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")
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
          Logger.error(s"Form error while changinge state of exercise $exId: ${formError.message}")
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
      toolMain.futureCompleteColls map { allColls =>
        Ok(views.html.admin.collExes.adminCollectionList(admin, allColls, toolMain, toolList))
      }
  }

  def adminNewCollectionForm(tool: String): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestCollectionId map { id =>
        val collection = toolMain.instantiateCollection(id + 1, ExerciseState.RESERVED)
        Ok(toolMain.renderCollectionEditForm(admin, collection, isCreation = true, toolList))
      }
  }

  def adminEditCollectionForm(tool: String, id: Int): EssentialAction = futureWithAdminWithToolMain(tool) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCompleteCollById(id) map { maybeCollection =>
        val collection = maybeCollection getOrElse toolMain.instantiateCollection(id, ExerciseState.RESERVED)
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
      toolMain.futureCompleteExesInColl(collId) map { exesInColl =>
        // FIXME: with collection?
        Ok(views.html.admin.collExes.adminCollExercisesOverview(admin, collId, exesInColl, toolMain, toolList))
      }
  }

  // User

  def collectionList(toolType: String, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteColls map { allColls =>
        val filteredColls = allColls filter (_.coll.state == ExerciseState.APPROVED)

        Ok(views.html.exercises.userCollectionsOverview(user, takeSlice(filteredColls, page), toolMain, page, filteredColls.size / stdStep + 1))
      }
  }

  def collection(toolType: String, id: Int, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteCollById(id) map {
        case None       => Redirect(controllers.routes.MainExerciseController.index(toolMain.urlPart))
        case Some(coll) =>
          val step = 18

          val exercises = coll.exercises.filter(_.ex.state == ExerciseState.APPROVED)

          val exesToDisplay = takeSlice(exercises, page, step)

          Ok(views.html.exercises.userCollectionExercisesOverview(user, coll, exesToDisplay, toolMain, page, step, exercises.size))
      }
  }

  def exercise(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      toolMain.futureCollById(collId) flatMap {
        case None             => Future(BadRequest(s"There is no collection with id $collId!"))
        case Some(collection) =>

          val values: Future[(Option[toolMain.CompExType], Int, Option[toolMain.DBSolType])] = for {
            compEx <- toolMain.futureCompleteExById(collId, id)
            numOfExes <- toolMain.numOfExesInColl(collId)
            oldSolution <- toolMain.futureMaybeOldSolution(user, collId, id)
          } yield (compEx, numOfExes, oldSolution)

          values map {
            case (None, _, _)                            => BadRequest(s"There is no exercise with id $id in collection $collId!")
            case (Some(ex), numOfExes, maybeOldSolution) => Ok(toolMain.renderExercise(user, collection, ex, numOfExes, maybeOldSolution))
          }
      }
  }

  def correctLive(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.correctAbstract(user, collId, id, isLive = true) map {
        case Success(result) => Ok(result)
        case Failure(error)  =>
          Logger.error("There has been an internal correction error:", error)
          BadRequest(toolMain.onLiveCorrectionError(error))
      }
  }

  def sampleSol(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.futureSampleSolutions(collId, id) map {
        sampleSolutions => Ok(JsArray(sampleSolutions map JsString.apply))
      }
  }

  def newExerciseForm(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureHighestIdInCollection(collId) map { highestId =>
        val newEx = toolMain.instantiateExercise(collId, highestId + 1, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(user, newEx, isCreation = true, toolList))
      }
  }

  def editExerciseForm(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(collId, exId) map {
        case None              => Ok(toolMain.renderExerciseEditForm(user, toolMain.instantiateExercise(collId, exId, ExerciseState.RESERVED), isCreation = true, toolList))
        case Some(newExercise) => Ok(toolMain.renderExerciseEditForm(user, newExercise, isCreation = false, toolList))
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
