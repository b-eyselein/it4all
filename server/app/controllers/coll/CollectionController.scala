package controllers.coll

import controllers.{AExerciseController, Secured}
import javax.inject.{Inject, Singleton}
import model.ExerciseFileJsonProtocol
import model.core._
import model.toolMains.{CollectionToolMain, ToolList}
import model.tools.programming.ProgToolMain
import model.tools.uml._
import model.tools.web.{WebExParts, WebToolMain}
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class CollectionController @Inject()(
  cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, ws: WSClient, val repository: Repository,
  progToolMain: ProgToolMain, umlToolMain: UmlToolMain, webToolMain: WebToolMain
)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl)
    with HasDatabaseConfigProvider[JdbcProfile]
    with Secured
    with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[CollectionController])

  override protected type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  override protected val adminRightsRequired: Boolean = false

  // Routes

  def index(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      for {
        allCollections <- toolMain.futureAllCollections
        allLearningPaths <- toolMain.futureLearningPaths
      } yield Ok("TODO!") // views.html.collectionExercises.collectionExercisesIndex(user, allCollections, toolMain, allLearningPaths))
  }

  def editExercise(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val onFormError: Form[toolMain.ExType] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          logger.error(s"The form has had an error for key '${formError.key}': " + formError.message)

        // FIXME: return in form...
        Future(BadRequest("TODO!"))
      }

      val onFormRead: toolMain.ExType => Future[Result] = { newExercise: toolMain.ExType =>
        toolMain.futureDeleteOldAndInsertNewExercise(collId, newExercise).map {
          case false =>
            // TODO: make view?
            BadRequest("Your exercise could not be saved...")
          case true  => Ok("TODO!") // views.html.admin.collExes.editCollectionExercisePreview(user, newExercise, toolMain))
        }
      }

      toolMain.exerciseForm.bindFromRequest().fold(onFormError, onFormRead)
  }

  def deleteExerciseInCollection(toolType: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(collId, exId).map {
        case false => BadRequest("TODO!")
        case true  => Ok(Json.obj("id" -> exId, "collId" -> collId))
      }
  }

  // Exercise review process

  def reviewExercisePart(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureCollById(collId) flatMap {
        case None             => Future.successful(onNoSuchCollection(user, toolMain, collId))
        case Some(collection) =>

          toolMain.futureExerciseById(collection.id, id) flatMap {
            case None           => Future.successful(onNoSuchExercise(user, toolMain, collection, id))
            case Some(exercise) =>

              toolMain.partTypeFromUrl(partStr) match {
                case None       => Future.successful(onNoSuchExercisePart(user, toolMain, collection, exercise, partStr))
                case Some(part) =>

                  val onFormError: Form[toolMain.ReviewType] => Future[Result] = { formWithErrors =>
                    ???
                  }

                  val onFormRead: toolMain.ReviewType => Future[Result] = { currentReview =>
                    toolMain.futureSaveReview(user.username, collId, exercise.id, part, currentReview).map {
                      case true  => ??? // Redirect(controllers.coll.routes.CollectionController.index(toolMain.urlPart))
                      case false => ???
                    }
                  }

                  toolMain.exerciseReviewForm.bindFromRequest().fold(onFormError, onFormRead)
              }
          }
      }
  }

  // Other routes

  def umlClassDiag(collId: Int, exId: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      def emptyClassDiagram: UmlClassDiagram = UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]())

      val futureClassDiagram: Future[UmlClassDiagram] = umlToolMain.partTypeFromUrl(partStr) match {
        case None       => Future(emptyClassDiagram)
        case Some(part) => umlToolMain.futureExerciseById(collId, exId) flatMap {
          case None                        =>
            logger.error(s"Error while loading uml class diagram for uml exercise $exId and part $part")
            Future.successful(emptyClassDiagram)
          case Some(exercise: UmlExercise) =>
            umlToolMain.futureMaybeOldSolution(user.username, collId, exId, part).map {
              case Some(solution) => solution.solution
              case None           => exercise.getDefaultClassDiagForPart(part)
            }
        }
      }

      futureClassDiagram.map { classDiagram =>
        Ok(Json.prettyPrint(UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat.writes(classDiagram))).as("text/javascript")
      }
  }

  def progClassDiagram(collId: Int, id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      progToolMain.futureCollById(collId) flatMap {
        case None             => Future.successful(onNoSuchCollection(user, progToolMain, collId))
        case Some(collection) =>

          progToolMain.futureExerciseById(collection.id, id).map {
            case None           => onNoSuchExercise(user, progToolMain, collection, id)
            case Some(exercise) =>

              val jsValue = exercise.maybeClassDiagramPart match {
                case Some(cd) => Json.toJson(cd)(UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat)
                case None     => JsObject.empty
              }
              Ok(jsValue) //.as("text/javascript")
          }
      }
  }

  def webSolution(collId: Int, exId: Int, partStr: String, fileName: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      webToolMain.futureCollById(collId) flatMap {
        case None             => Future.successful(onNoSuchCollection(user, webToolMain, collId))
        case Some(collection) =>

          webToolMain.futureExerciseById(collection.id, exId) flatMap {
            case None           => Future.successful(onNoSuchExercise(user, webToolMain, collection, exId))
            case Some(exercise) =>

              webToolMain.partTypeFromUrl(partStr) match {
                case None    => Future.successful(onNoSuchExercisePart(user, webToolMain, collection, exercise, partStr))
                case Some(_) =>

                  val contentType: String = fileName.split("\\.").last match {
                    case "html" => "text/html"
                    case "css"  => "text/css"
                    case "js"   => "text/javascript"
                    case _      => "text/plain"
                  }

                  ws.url(webToolMain.getSolutionUrl(user, collId, exId, fileName)).get()
                    .map(wsRequest => Ok(wsRequest.body).as(contentType))
              }
          }
      }
  }

  def updateWebSolution(collId: Int, id: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      request.body.asJson match {
        case Some(jsValue) =>
          ExerciseFileJsonProtocol.exerciseFileWorkspaceReads.reads(jsValue) match {
            case JsSuccess(ideWorkSpace, _) =>

              webToolMain.writeWebSolutionFiles(user.username, collId, id, webToolMain.partTypeFromUrl(part).getOrElse(WebExParts.HtmlPart), ideWorkSpace.files) match {
                case Success(_)     => Ok("Solution saved")
                case Failure(error) =>
                  logger.error("Error while updating web solution", error)
                  BadRequest("Solution was not saved!")
              }

            case _ => ???
          }

        case _ => BadRequest("No or wrong content!")
      }
  }

}
