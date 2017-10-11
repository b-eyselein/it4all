package controllers.sql

import javax.inject.Inject

import controllers.core.AExerciseCollectionAdminController
import model.SqlScenarioReader
import model.exercise.{SqlExercise, SqlScenario}
import model.user.User
import play.data.FormFactory
import play.db.{Database, NamedDatabase}
import play.mvc.{Result, Results}
import play.twirl.api.Html

class SqlAdmin @Inject()(f: FormFactory,
                         @NamedDatabase("sqlselectroot") sqlSelect: Database,
                         @NamedDatabase("sqlotherroot") sqlOther: Database)
  extends AExerciseCollectionAdminController[SqlExercise, SqlScenario](f, SqlToolObject, SqlScenario.finder, new SqlScenarioReader(sqlSelect, sqlOther)) {


  override def newExerciseForm: Result = Results.ok(views.html.sqlAdmin.newExerciseForm.render(getUser, null))

  override protected def statistics: Html = {
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
  }

  // override
  // public Html renderCollectionCreated(List<SqlScenario> created) {
  // return views.html.sqlAdmin.sqlCreation.render(created)
  // }

  override def renderCollectionCreated(collections: List[SqlScenario], created: Boolean): Html = ??? // FIXME: implement...

  override def renderExCollCreationForm(user: User, scenario: SqlScenario): Html =
    views.html.sqlAdmin.newScenarioForm.render(user, scenario)


  override def renderExEditForm(user: User, exercise: SqlScenario, isCreation: Boolean): Html = ??? // FIXME: implement...

  override def renderExerciseCollections(user: User, allCollections: List[SqlScenario]): Html = ??? // FIXME: implement...

  def scenarioAdmin(id: Int): Result = Results.ok(views.html.sqlAdmin.scenarioAdmin.render(getUser, SqlScenario.finder.byId(id)))


}
