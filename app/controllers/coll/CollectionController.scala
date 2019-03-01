package controllers.coll

import controllers.{AFixedExController, Secured}
import javax.inject.{Inject, Singleton}
import model.ExerciseState
import model.core.CoreConsts._
import model.core._
import model.core.overviewHelpers.{SolvedStates, UserCollEx}
import model.tools.programming.ProgToolMain
import model.toolMains.{CollectionExIdentifier, CollectionToolMain, ToolList}
import model.tools.web.{WebExParts, WebToolMain}
import model.tools.uml._
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CollectionController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, ws: WSClient, val repository: Repository,
                                     progToolMain: ProgToolMain, umlToolMain: UmlToolMain, webToolMain: WebToolMain)(implicit ec: ExecutionContext)
  extends AFixedExController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  override protected type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  override protected val adminRightsRequired: Boolean = true

  //  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  private def takeSlice[T](collection: Seq[T], page: Int, step: Int = stdStep): Seq[T] = {
    val start = Math.max(0, (page - 1) * step)
    val end = Math.min(page * step, collection.size)

    collection slice(start, end)
  }

  // User

  def index(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      for {
        allCollections <- toolMain.futureAllCollections
        allLearningPaths <- toolMain.futureLearningPaths
      } yield Ok(views.html.collectionExercises.collectionExercisesIndex(user, allCollections, toolMain, allLearningPaths))
  }

  //  def collectionList(toolType: String, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
  //    implicit request =>
  //      toolMain.futureAllCollections map { allColls =>
  //        val filteredColls = allColls filter (_.state == ExerciseState.APPROVED)
  //
  //        Ok(views.html.collectionExercises.userCollectionsOverview(user, takeSlice(filteredColls, page), toolMain, page, filteredColls.size / stdStep + 1))
  //      }
  //  }

  def collection(toolType: String, collId: Int, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      val step = 18

      toolMain.futureCollById(collId) flatMap {
        case None                          => Future(Redirect(controllers.routes.MainExerciseController.index(toolMain.urlPart)))
        case Some(coll: toolMain.CollType) =>
          toolMain.futureExercisesInColl(coll.id) flatMap { exercises =>


            val approvedExercises: Seq[toolMain.ExType] = exercises.filter(_.state == ExerciseState.APPROVED)

            val exesToDisplay = takeSlice(approvedExercises, page, step)

            val futureExesAndSuccessTypes: Future[Seq[UserCollEx]] = Future.sequence(exesToDisplay.map {
              ex: toolMain.ExType =>
                toolMain.futureSolveState(user, collId, ex.id) map {
                  // FIXME: query solved state!
                  maybeSolvedState => UserCollEx(ex, maybeSolvedState getOrElse SolvedStates.NotStarted)
                }
            })

            futureExesAndSuccessTypes map {
              exesAndSuccessTypes =>
                Ok(views.html.collectionExercises.userCollectionExercisesOverview(
                  user, coll, exesAndSuccessTypes, toolMain, page, step, approvedExercises.size))
            }
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

              toolMain.futureExerciseById(collId, id) flatMap {
                case None           => Future.successful(onNoSuchExercise(id))
                case Some(exercise) =>
                  toolMain.futureMaybeOldSolution(user, exIdentifier, exPart) map {
                    maybeOldSolution => Ok(toolMain.renderExercise(user, collection, exercise, exPart, maybeOldSolution))
                  }
              }
          }
      }
  }

  def correctLive(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None         => Future.successful(onNoSuchExercisePart(partStr))
        case Some(exPart) =>
          toolMain.futureCollById(collId) flatMap {
            case None             => Future.successful(onNoSuchCollection(collId))
            case Some(collection) =>
              toolMain.futureExerciseById(collection.id, id) flatMap {
                case None           => Future.successful(onNoSuchExercise(id))
                case Some(exercise) =>
                  toolMain.correctAbstract(user, collection, exercise, exPart) map {
                    case Success(result) => Ok(result)
                    case Failure(error)  =>
                      Logger.error("There has been an internal correction error:", error)
                      BadRequest(toolMain.onLiveCorrectionError(error))
                  }
              }
          }
      }
  }

  def sampleSol(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future.successful(onNoSuchExercisePart(partStr))
        case Some(part) =>
          toolMain.futureSampleSolutions(collId, id, part) map {
            sampleSolutions =>
              println(sampleSolutions)
              Ok(JsArray(sampleSolutions map JsString.apply))
          }
      }
  }

  def newExerciseForm(toolType: String, collId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureHighestExerciseIdInCollection(collId) map { highestId =>
        val newEx = toolMain.instantiateExercise(highestId + 1, user.username, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(user, collId, newEx, isCreation = true, toolList))
      }
  }

  def editExerciseForm(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureExerciseById(collId, exId) map {
        case Some(newExercise) => Ok(toolMain.renderExerciseEditForm(user, collId, newExercise, isCreation = false, toolList))
        case None              =>
          val newExercise = toolMain.instantiateExercise(exId, user.username, ExerciseState.RESERVED)
          Ok(toolMain.renderExerciseEditForm(user, collId, newExercise, isCreation = true, toolList))
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
        toolMain.futureInsertExercise(collId, newExercise) map {
          case false =>
            // TODO: make view?
            BadRequest("Your exercise could not be saved...")
          case true  => Ok(views.html.admin.collExes.editCollectionExercisePreview(user, newExercise, toolMain))
        }
      }

      toolMain.exerciseForm.bindFromRequest().fold(onFormError, onFormRead)
  }

  def deleteExerciseInCollection(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(collId, exId) map {
        case false => BadRequest("TODO!")
        case true  => Ok(Json.obj("id" -> exId, "collId" -> collId))
      }
  }

  // Exercise review process

  def reviewExercisePartForm(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future.successful(onNoSuchExercisePart(partStr))
        case Some(part) =>
          toolMain.futureExerciseById(collId, id) map {
            case None           => onNoSuchExercise(id)
            case Some(exercise) => Ok(views.html.idExercises.evaluateExerciseForm(user, collId, exercise, part, toolMain))
          }
      }
  }

  def reviewExercisePart(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) => toolMain.futureExerciseById(collId, id) flatMap {
          case None           => Future(onNoSuchExercise(id))
          case Some(exercise) =>
            val onFormError: Form[toolMain.ReviewType] => Future[Result] = { formWithErrors =>
              ???
            }

            val onFormRead: toolMain.ReviewType => Future[Result] = { currentReview =>
              toolMain.futureSaveReview(user.username, collId, exercise.id, part, currentReview) map {
                case true  => Redirect(controllers.routes.MainExerciseController.index(toolMain.urlPart))
                case false => ???
              }
            }

            toolMain.exerciseReviewForm.bindFromRequest().fold(onFormError, onFormRead)
        }
      }
  }


  // Other routes

  def umlClassDiag(collId: Int, id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      def emptyClassDiagram: UmlClassDiagram = UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]())

      val futureClassDiagram: Future[UmlClassDiagram] = umlToolMain.partTypeFromUrl(partStr) match {
        case None       => Future(emptyClassDiagram)
        case Some(part) => umlToolMain.futureExerciseById(collId, id) flatMap {
          case None                        =>
            Logger.error(s"Error while loading uml class diagram for uml exercise $id and part $part")
            Future.successful(emptyClassDiagram)
          case Some(exercise: UmlExercise) =>
            val exIdentifier = CollectionExIdentifier(collId, id)
            umlToolMain.futureMaybeOldSolution(user, exIdentifier, part) map {
              case Some(solution) => solution.solution
              case None           => exercise.getDefaultClassDiagForPart(part)
            }
        }
      }

      futureClassDiagram map { classDiagram =>
        Ok(Json.prettyPrint(UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(classDiagram))).as("text/javascript")
      }
  }

  def progClassDiagram(collId: Int, id: Int): EssentialAction = futureWithUser { _ =>
    implicit request =>
      progToolMain.futureExerciseById(collId, id) map {
        case None           => onNoSuchExercise(id)
        case Some(exercise) =>
          val jsValue = exercise.maybeClassDiagramPart match {
            case Some(cd) => Json.toJson(cd)(UmlClassDiagramJsonFormat.umlSolutionJsonFormat)
            case None     => JsObject.empty
          }
          Ok(jsValue) //.as("text/javascript")
      }
  }

  def webSolution(collId: Int, id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      webToolMain.partTypeFromUrl(partStr) match {
        case None       => Future(onNoSuchExercisePart(partStr))
        case Some(part) => ws.url(webToolMain.getSolutionUrl(user, id, part)).get() map (wsRequest => Ok(wsRequest.body).as("text/html"))
      }
  }

  def updateWebSolution(collId: Int, id: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      request.body.asText match {
        case None       => BadRequest("No content!")
        case Some(text) =>
          webToolMain.writeWebSolutionFile(user.username, id, webToolMain.partTypeFromUrl(part).getOrElse(WebExParts.HtmlPart), text)
          //          match {
          //            case Success(_)     =>
          Ok("Solution saved")
        //            case Failure(error) =>
        //              Logger.error("Error while updating web solution", error)
        //              BadRequest("Solution was not saved!")
        //          }
      }
  }

}
