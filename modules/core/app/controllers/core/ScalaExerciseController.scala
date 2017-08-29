package controllers.core

import scala.annotation.implicitNotFound
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import io.ebean.Finder
import model.Secured
import model.exercise.Exercise
import model.result.EvaluationResult
import model.user.User
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.mvc.AbstractController
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import play.mvc.Security.Authenticated
import play.twirl.api.Html

@Authenticated(classOf[Secured])
abstract class ScalaExerciseController[E <: Exercise, R <: EvaluationResult](cc: ControllerComponents, val exerciseType: String, finder: Finder[Integer, E])
    extends AbstractController(cc) {

  case class LearnerSolution(sol: String)

  val solutionForm = Form(mapping(model.StringConsts.FORM_VALUE -> text)(LearnerSolution.apply)(LearnerSolution.unapply))

  def getUser(request: Request[AnyContent]) = User.finder.byId(getUsername(request))

  def getUsername(request: Request[AnyContent]) = request.session(model.StringConsts.SESSION_ID_FIELD)

  def correct(id: Int) = Action { implicit request =>
    val user = getUser(request)
    val exercise = finder.byId(id)
    val learnerSolution = solutionForm.bindFromRequest.get

    correctExercise(learnerSolution.sol, exercise, user) match {
      case Success(correctionResult) => {
        // log(user, new ExerciseCompletionEvent(request(), id, correctionResult))
        Ok(views.html.correction.render(exerciseType.toUpperCase(), renderResult(correctionResult),
          learnerSolution.sol, user, controllers.routes.Application.index()))
      }
      case Failure(_) => BadRequest("TODO!")
    }
  }

  def correctLive(id: Int) = Action { implicit request =>
    val exercise = finder.byId(id)
    val learnerSolution = solutionForm.bindFromRequest.get

    correctExercise(learnerSolution.sol, exercise, getUser(request)) match {
      case Success(correctionResult) => {
        // log(user, new ExerciseCompletionEvent(request(), id, correctionResult))
        Ok(renderResult(correctionResult))
      }
      case Failure(_) => BadRequest("TODO!")
    }
  }

  def exercise(id: Int) = Action { request =>
    val exercise: E = finder.byId(id)

    if (exercise == null)
      Redirect(controllers.routes.Application.index())

    //    log(user, new ExerciseStartEvent(request.asJava., id))

    Ok(renderExercise(getUser(request), exercise))
  }

  def exercises() = Action { request =>
    Ok(renderExercises(getUser(request), finder.all().asScala.toList))
  }

  //  protected Path checkAndCreateSolDir(String username, Exercise exercise) {
  //    Path dir = getSolDirForExercise(username, exerciseType, exercise)
  //
  //    if(dir.toFile().exists())
  //      return dir
  //
  //    try {
  //      return Files.createDirectories(dir)
  //    } catch (IOException e) {
  //      Logger.error("There was an error while creating the directory for an " + exerciseType + " solution: " + dir, e)
  //      return null
  //    }
  //  }
  //
  //
  //  protected Path getSampleDir() {
  //    return getSampleDir(exerciseType)
  //  }

  def correctExercise(learnerSolution: String, exercise: E, user: User): Try[List[R]]

  def renderExercise(user: User, exercise: E): Html

  def renderExercises(user: User, exercises: List[E]): Html

  def renderResult(correctionResult: List[R]): Html

}

