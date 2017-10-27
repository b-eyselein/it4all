package controllers.sql

import javax.inject._

import controllers.core.excontrollers.AExerciseCollectionAdminController
import model.User
import model.core._
import model.sql._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Reads
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

class SqlAdmin @Inject()
(cc: ControllerComponents /*, @NamedDatabase("sqlselectroot") sqlSelect: Database, @NamedDatabase("sqlotherroot") sqlOther: Database*/ ,
 dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AExerciseCollectionAdminController[SqlExercise, SqlScenario](cc, dbcp, r, SqlToolObject) with Secured {

  override def reads: Reads[SqlScenario] = SqlReads.sqlScenarioReads

  override type TQ = repo.SqlScenarioesTable

  override def tq = repo.sqlScenarioes

  override def newExerciseForm: EssentialAction = withAdmin { user => implicit request => Ok(views.html.sql.newExerciseForm.render(user, null)) }

  override protected def statistics: Future[Html] = Future(
    //    return new Html("<li>
    //            Es gibt insgesamt @model.exercise.SqlScenario.finder.all.size Szenarien
    //            <ul>
    //    @for(scenario <- model.exercise.SqlScenario.finder.all) {
    //    <li>Das Szenario &quot<a href="@sql.routes.SqlAdmin.scenarioAdmin(scenario.getId)">@scenario.getTitle</a>&quot
    //      hat @scenario.getExercises.size Aufgaben</li>
    //    }
    //  </ul>
    //</li>")
    new Html("")
  )

  // override
  // public Html renderCollectionCreated(List<SqlScenario> created) {
  // return views.html.sqlAdmin.sqlCreation.render(created)
  // }

  override def renderCollectionCreated(collections: List[SingleReadingResult[SqlScenario]]): Html = ??? // FIXME: implement...

  override def renderExCollCreationForm(user: User, scenario: SqlScenario): Html =
    views.html.sql.newScenarioForm.render(user, scenario)


  override def renderExEditForm(user: User, exercise: SqlScenario, isCreation: Boolean): Html = ??? // FIXME: implement...

  override def renderExerciseCollections(user: User, allCollections: List[SqlScenario]): Html = ??? // FIXME: implement...

  def scenarioAdmin(id: Int): EssentialAction = withAdmin { user => implicit request => Ok(views.html.sql.scenarioAdmin.render(user, null /* SqlScenario.finder.byId(id)*/)) }


}
