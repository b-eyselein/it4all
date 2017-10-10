package controllers.sql;

import controllers.core.AExerciseCollectionAdminController;
import model.SqlScenarioReader;
import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import model.user.User;
import play.api.Configuration;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.inject.Inject;
import java.util.List;

public class SqlAdmin extends AExerciseCollectionAdminController<SqlExercise, SqlScenario> {

    @Inject
    public SqlAdmin(Configuration c, FormFactory f, @NamedDatabase("sqlselectroot") Database theSqlSelect,
                    @NamedDatabase("sqlotherroot") Database theSqlOther) {
        super(c, f, new SqlToolObject(c), SqlScenario.finder, new SqlScenarioReader(theSqlSelect, theSqlOther));
    }

    @Override
    public Result newExerciseForm() {
        return ok(views.html.sqlAdmin.newExerciseForm.render(getUser(), null));
    }

    @Override
    public Html renderAdminIndex(User user) {
        return views.html.admin.collectionAdminMain.render(user, statistics(), toolObject(), new Html(""));
    }

    protected Html statistics() {
//    return new Html("<li>
//            Es gibt insgesamt @model.exercise.SqlScenario.finder.all.size Szenarien
//            <ul>
//    @for(scenario <- model.exercise.SqlScenario.finder.all) {
//    <li>Das Szenario &quot;<a href="@sql.routes.SqlAdmin.scenarioAdmin(scenario.getId)">@scenario.getTitle</a>&quot;
//      hat @scenario.getExercises.size Aufgaben</li>
//    }
//  </ul>
//</li>")
        return new Html("");
    }

    // @Override
    // public Html renderCollectionCreated(List<SqlScenario> created) {
    // return views.html.sqlAdmin.sqlCreation.render(created);
    // }

    @Override
    public Html renderCollectionCreated(List<SqlScenario> collections, boolean created) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Html renderExCollCreationForm(User user, SqlScenario scenario) {
        return views.html.sqlAdmin.newScenarioForm.render(getUser(), scenario);
    }

    // @Override
    // public Html renderExEditForm(User user, SqlExercise exercise, boolean
    // isCreation) {
    // // TODO Auto-generated method stub
    // return null;
    // }

    @Override
    public Html renderExEditForm(User user, SqlScenario exercise, boolean isCreation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Html renderExerciseCollections(User user, List<SqlScenario> allCollections) {
        // TODO Auto-generated method stub
        return null;
    }

    public Result scenarioAdmin(int id) {
        return ok(views.html.sqlAdmin.scenarioAdmin.render(getUser(), SqlScenario.finder.byId(id)));
    }

}
