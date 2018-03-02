package model.toolMains

import model.Enums.ExerciseState
import model.core.ExPart
import model.{SingleCompleteEx, User}
import net.jcazevedo.moultingyaml.Auto
import play.api.mvc.Call
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

abstract class ASingleExerciseToolMain(urlPart: String) extends FixedExToolMain(urlPart) {

  // Abstract Types

  type PartType <: ExPart

  override type CompExType <: SingleCompleteEx[ExType, PartType]

  override type ReadType = CompExType

  // Other members

  val exParts: Seq[PartType]


  // DB

  def futureCompleteExById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompExType]] = tables.futureCompleteExById(id)

  override def futureSaveRead(exercises: Seq[ReadType])(implicit ec: ExecutionContext): Future[Seq[(ReadType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.saveCompleteEx(ex) map (saveRes => (ex, saveRes))
  })

  def futureHighestId(implicit ec: ExecutionContext): Future[Int] = tables.futureHighestId

  // Helper methods

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  def dataForUserExesOverview(page: Int)(implicit ec: ExecutionContext): Future[UserExOverviewContent] = {
    // FIXME: check needed values, implement!

    val ret: Future[UserExOverviewContent] = for {
      numOfExes <- tables.futureNumOfExes
      exes <- tables.futureCompleteExesForPage(page)
      exesAndRoutes: Seq[ExAndRoute] = exes map (ex => ExAndRoute(ex.wrapped, exerciseRoutes(ex)))
    } yield UserExOverviewContent(numOfExes, exesAndRoutes)

    ret
  }

  // Helper methods for admin

  def reserveExercise(implicit ec: ExecutionContext): Future[CompExType] = tables.futureHighestId map { highestId =>
    val compExercise = instantiateExercise(highestId + 1, ExerciseState.RESERVED)

    tables.saveCompleteEx(compExercise)

    compExercise
  }

  def instantiateExercise(id: Int, state: ExerciseState): CompExType

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString(implicit ec: ExecutionContext): Future[String] = futureCompleteExes map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

  // Views

  //FIXME: ugly hack because of type params...
  def renderEditForm(id: Int, admin: User)(implicit ec: ExecutionContext): Future[Html] = futureCompleteExById(id) map {
    case Some(ex) => views.html.admin.exerciseEditForm(admin, this, ex, renderEditRest(ex), isCreation = false)
    case None     => ???
  }

  // Routes

  def exerciseRoutes(exercise: CompExType): Seq[(PartType, Call)] = exParts flatMap {
    exPart: PartType =>
      // FIXME: check if
      // - exercise has such a part
      // - user can solve this part!

      if (exercise.hasPart(exPart)) {

        this match {
          case _: FileExerciseToolMain => Some((exPart, controllers.exes.routes.FileExerciseController.exercise(urlPart, exercise.ex.id, exPart.urlName)))
          case _: AExerciseToolMain    => Some((exPart, controllers.exes.routes.ExerciseController.exercise(urlPart, exercise.ex.id, exPart.urlName)))
        }

      } else None
  }

}
