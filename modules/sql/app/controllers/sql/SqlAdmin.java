package controllers.sql;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.SqlExerciseReader;
import model.StringConsts;
import model.Util;
import model.exercise.SqlScenario;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.twirl.api.Html;

public class SqlAdmin extends AbstractAdminController<SqlScenario, SqlExerciseReader> {
  
  @Inject
  public SqlAdmin(Util theUtil, FormFactory theFactory) {
    super(theUtil, theFactory, SqlScenario.finder, "sql", new SqlExerciseReader());
  }
  
  public Result index() {
    return ok(views.html.sqlAdmin.index.render(getUser()));
  }
  
  public Result newScenario() {
    DynamicForm form = factory.form().bindFromRequest();
    SqlScenario scenario = new SqlScenario(findMinimalNotUsedId(finder));

    scenario.author = form.get(StringConsts.AUTHOR_NAME);
    scenario.scriptFile = form.get("scriptFile");
    scenario.shortName = form.get("shortName");
    scenario.text = form.get(StringConsts.TEXT_NAME);
    scenario.title = form.get(StringConsts.TITLE_NAME);

    scenario.saveInDB();

    return ok(views.html.preview.render(getUser(), views.html.sqlAdmin.sqlCreation.render(scenario)));
  }
  
  public Result newScenarioForm() {
    return ok(views.html.sqlAdmin.newScenarioForm.render(getUser()));
  }
  
  @Override
  public Html renderCreated(List<SqlScenario> created) {
    // Guaranteed to be always one scenario by json Schema!
    return views.html.sqlAdmin.sqlCreation.render(created.get(0));
  }
  
  public Result scenarioAdmin(int id) {
    return ok(views.html.sqlAdmin.scenarioAdmin.render(getUser(), SqlScenario.finder.byId(id)));
  }
  
  @Override
  public Result uploadFile() {
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_FILE_NAME);
    if(uploadedFile == null)
      return badRequest("Fehler!");
    
    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(util.getRootSolDir().toString(), "admin", exerciseType);
    Path saveTo = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, saveTo);
    
    // List<SqlScenario> results = exerciseReader.readScenarioes(saveTo);
    //
    // saveScenarioes(results);
    //
    // return ok(views.html.preview.render(getUser(),
    // views.html.sqlcreation.render(results)));
    
    return ok("TODO!");
  }
  
  @Override
  public Result uploadForm() {
    return ok(views.html.sqlupload.render(getUser()));
  }
  
}
