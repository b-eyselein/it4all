package controllers.coll

import controllers.{AFixedExController, Secured}
import javax.inject.{Inject, Singleton}
import model.ExerciseState
import model.core.CoreConsts._
import model.core._
import model.core.overviewHelpers.{SolvedStates, UserCollEx}
import model.toolMains.{CollectionExIdentifier, CollectionToolMain, ToolList}
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, JsString, Json}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CollectionController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AFixedExController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  override protected type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  override protected val adminRightsRequired: Boolean = true

  // Helpers

//  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  private def takeSlice[T](collection: Seq[T], page: Int, step: Int = stdStep): Seq[T] = {
    val start = Math.max(0, (page - 1) * step)
    val end = Math.min(page * step, collection.size)

    collection slice(start, end)
  }

  // User

  def collectionList(toolType: String, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteColls map { allColls =>
        val filteredColls = allColls filter (_.coll.state == ExerciseState.APPROVED)

        Ok(views.html.exercises.userCollectionsOverview(user, takeSlice(filteredColls, page).map(_.coll), toolMain, page, filteredColls.size / stdStep + 1))
      }
  }

  def collection(toolType: String, id: Int, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCompleteCollById(id) flatMap {
        case None                              => Future(Redirect(controllers.routes.MainExerciseController.index(toolMain.urlPart)))
        case Some(coll: toolMain.CompCollType) =>
          val step = 18

          val approvedExercises: Seq[coll.CompEx] = coll.exercises.filter(_.state == ExerciseState.APPROVED)

          val exesToDisplay = takeSlice(approvedExercises, page, step)

          val futureExesAndSuccessTypes: Future[Seq[UserCollEx]] = Future.sequence(exesToDisplay.map {
            ex: coll.CompEx =>
              toolMain.futureSolveState(user, ex.collectionId, ex.id) map {
                // FIXME: query solved state!
                maybeSolvedState => UserCollEx(ex, maybeSolvedState getOrElse SolvedStates.NotStarted)
              }
          })

          futureExesAndSuccessTypes map {
            exesAndSuccessTypes =>
              Ok(views.html.exercises.userCollectionExercisesOverview(
                user, coll.coll, exesAndSuccessTypes, toolMain, page, step, approvedExercises.size))
          }
      }
  }

  def exercise(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      toolMain.partTypeFromUrl(partStr) match {
        case None         => Future.successful(onNoSuchExercisePart(partStr))
        case Some(exPart) =>

          toolMain.futureCollById(collId) flatMap {
            case None             => Future(BadRequest(s"There is no collection with id $collId!"))
            case Some(collection) =>
              val exIdentifier = CollectionExIdentifier(collId, id)


              val values: Future[(Option[toolMain.ExType], Int, Option[toolMain.DBSolType])] = for {
                compEx <- toolMain.futureExerciseById(collId, id)
                numOfExes <- toolMain.numOfExesInColl(collId)
                oldSolution <- toolMain.futureMaybeOldSolution(user, exIdentifier, exPart)
              } yield (compEx, numOfExes, oldSolution)

              values map {
                case (None, _, _)                            => BadRequest(s"There is no exercise with id $id in collection $collId!")
                case (Some(ex), numOfExes, maybeOldSolution) => Ok(toolMain.renderExercise(user, collection, ex, numOfExes, maybeOldSolution, exPart))
              }
          }
      }
  }

  def correctLive(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None         => Future.successful(onNoSuchExercisePart(partStr))
        case Some(exPart) =>
          toolMain.correctAbstract(user, collId, id, exPart) map {
            case Success(result) => Ok(result)
            case Failure(error)  =>
              Logger.error("There has been an internal correction error:", error)
              BadRequest(toolMain.onLiveCorrectionError(error))
          }
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
        val newEx = toolMain.instantiateExercise(collId, highestId + 1, user.username, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(user, newEx, isCreation = true, toolList))
      }
  }

  def editExerciseForm(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureExerciseById(collId, exId) map {
        case Some(newExercise) => Ok(toolMain.renderExerciseEditForm(user, newExercise, isCreation = false, toolList))
        case None              =>
          val newExercise = toolMain.instantiateExercise(collId, exId, user.username, ExerciseState.RESERVED)
          Ok(toolMain.renderExerciseEditForm(user, newExercise, isCreation = true, toolList))
      }
  }


  def editExercise(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val onFormError: Form[toolMain.ExType] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(s"The form has had an error for key '${formError.key}': " + formError.message)

        // FIXME: return in form...
        Future(BadRequest("TODO!"))
      }

      val onFormRead: toolMain.ExType => Future[Result] = { newExercise: toolMain.ExType =>
        toolMain.futureInsertExercise(newExercise) map {
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
