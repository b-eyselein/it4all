package model.toolMains

import model.Enums.ExerciseState
import model.core.{ExPart, ExerciseFormMappings, ReadAndSaveResult}
import model.learningPath.LearningPath
import model.persistence.IdExerciseTableDefs
import model.{SingleCompleteEx, User}
import net.jcazevedo.moultingyaml.Auto
import play.api.data.Form
import play.api.mvc.{AnyContent, Call, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

abstract class ASingleExerciseToolMain(urlPart: String)(implicit ec: ExecutionContext) extends FixedExToolMain(urlPart) with ExerciseFormMappings {

  // Abstract Types

  type PartType <: ExPart

  override type CompExType <: SingleCompleteEx[ExType, PartType]

  override type Tables <: IdExerciseTableDefs[ExType, CompExType]

  override type ReadType = CompExType

  // Other members

  val exParts: Seq[PartType]

  implicit val compExForm: Form[ExType]

  // DB

  def futureUpdateExercise(exercise: ExType): Future[Boolean] = tables.futureUpdateExercise(exercise)

  def futureCompleteExById(id: Int): Future[Option[CompExType]] = tables.futureCompleteExById(id)

  override def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.saveCompleteEx(ex) map (saveRes => (ex, saveRes))
  })

  def futureHighestId: Future[Int] = tables.futureHighestExerciseId

  def updateExerciseState(id: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(id, newState)

  def futureDeleteExercise(id: Int): Future[Int] = tables.deleteExercise(id)

  // Helper methods

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  def dataForUserExesOverview(page: Int): Future[UserExOverviewContent] = {
    // FIXME: check needed values, implement!

    val ret: Future[UserExOverviewContent] = for {
      numOfExes <- tables.futureNumOfExes
      exes <- tables.futureCompleteExesForPage(page)
      exesAndRoutes: Seq[ExAndRoute] = exes map (ex => ExAndRoute(ex, exerciseRoutes(ex)))
    } yield UserExOverviewContent(numOfExes, exesAndRoutes)

    ret
  }

  // Helper methods for admin

  def reserveExercise: Future[CompExType] = tables.futureHighestExerciseId map { highestId =>
    val compExercise = instantiateExercise(highestId + 1, ExerciseState.RESERVED)

    tables.saveCompleteEx(compExercise)

    compExercise
  }

  def instantiateExercise(id: Int, state: ExerciseState): CompExType

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString: Future[String] = futureCompleteExes map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

  def readEditFromForm(implicit request: Request[AnyContent]): Form[ExType] = compExForm.bindFromRequest()

  // Views

  def renderExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean): Html =
    views.html.admin.exerciseEditForm(user, this, newEx, renderEditRest(newEx), isCreation = true)

  def adminExerciseList(admin: User, exes: Seq[CompExType]): Html

  // Routes

  def exerciseRoutes(exercise: CompExType): Seq[(PartType, Call)] = exParts flatMap {
    exPart: PartType =>
      // FIXME: check if user can solve this part!

      if (exercise.hasPart(exPart)) {

        this match {
          case _: FileExerciseToolMain => Some((exPart, controllers.routes.FileExerciseController.exercise(urlPart, exercise.ex.id, exPart.urlName)))
          case _: IdExerciseToolMain   => Some((exPart, controllers.routes.ExerciseController.exercise(urlPart, exercise.ex.id, exPart.urlName)))
        }

      } else None
  }

}
