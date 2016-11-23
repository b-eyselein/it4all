package controllers.sql;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.inject.Inject;

import controllers.core.AdminController;
import controllers.core.UserManagement;
import model.Util;
import model.creation.ScenarioCreationResult;
import model.creation.SqlScenarioHandler;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import play.Logger;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import views.html.sqlpreview;
import views.html.sqlupload;

public class SQLAdmin extends AdminController {

  @Inject
  public SQLAdmin(Util theUtil) {
    super(theUtil, "sql");
  }

  private static void readExercise(String scenarioName, int id, Map<String, String[]> data) {
    SqlExerciseType exerciseType = SqlExerciseType.valueOf(data.get("ex" + id + "_type")[0]);

    SqlExerciseKey key = new SqlExerciseKey(scenarioName, id, exerciseType);

    SqlExercise exercise = SqlExercise.finder.byId(key);
    if(exercise == null)
      exercise = new SqlExercise(key);
    exercise.samples = String.join("#", data.get("ex" + id + "_samples[]"));
    exercise.text = data.get("ex" + id + "_text")[0];
    exercise.save();
  }

  @Override
  public Result create() {
    Map<String, String[]> data = request().body().asFormUrlEncoded();

    String shortname = data.get("shortname")[0];
    String longname = data.get("longname")[0];
    String scriptfile = data.get("scriptfile")[0];

    SqlScenario newScenario = SqlScenario.finder.byId(shortname);
    if(newScenario == null)
      newScenario = new SqlScenario(shortname);
    newScenario.longName = longname;
    newScenario.scriptFile = scriptfile;
    newScenario.save();

    Logger.debug("Data: " + data);

    String[] ids = data.get("id[]");
    for(String id: ids) {
      readExercise(shortname, Integer.parseInt(id), data);
    }

    // FIXME: render success!
    return ok("This has still got to be implemented...");
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

    ScenarioCreationResult scenarioResult = SqlScenarioHandler.handleScenario(saveTo);

    return ok(sqlpreview.render(UserManagement.getCurrentUser(), scenarioResult));
  }

  @Override
  public Result uploadForm() {
    return ok(sqlupload.render(UserManagement.getCurrentUser()));
  }

}
