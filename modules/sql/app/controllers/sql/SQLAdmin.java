package controllers.sql;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import javax.inject.Inject;

import controllers.core.UserManagement;
import model.AdminSecured;
import model.Util;
import model.creation.ScenarioCreationResult;
import model.creation.SqlScenarioHandler;
import model.exercise.SqlExercise;
import model.exercise.SqlExerciseKey;
import model.exercise.SqlExerciseType;
import model.exercise.SqlScenario;
import play.Logger;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.sqlpreview;
import views.html.sqlupload;

@Authenticated(AdminSecured.class)
public class SQLAdmin extends Controller {

  private static String BODY_FILE_NAME = "file";

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

  @Inject
  private Util util;

  @Inject
  @NamedDatabase("sqlotherroot")
  private Database sql_other;

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

  public Result uploadFile() {
    // Extract solution from request
    MultipartFormData<File> body = request().body().asMultipartFormData();
    FilePart<File> uploadedFile = body.getFile(BODY_FILE_NAME);
    if(uploadedFile == null)
      return badRequest("Fehler!");

    Path pathToUploadedFile = uploadedFile.getFile().toPath();
    Path savingDir = Paths.get(util.getRootSolDir().toString(), "admin", "sql");
    Path saveTo = Paths.get(savingDir.toString(), uploadedFile.getFilename());
    saveUploadedFile(savingDir, pathToUploadedFile, saveTo);

    // TODO: return exercises!
    ScenarioCreationResult scenarioResult = SqlScenarioHandler.handleScenario(saveTo, sql_other);

    return ok(sqlpreview.render(UserManagement.getCurrentUser(), scenarioResult));
  }

  public Result uploadForm() {
    return ok(sqlupload.render(UserManagement.getCurrentUser()));
  }

  private void saveUploadedFile(Path savingDir, Path pathToUploadedFile, Path saveTo) {
    try {
      if(!Files.exists(savingDir) && !Files.isDirectory(savingDir))
        Files.createDirectories(savingDir);
      Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      Logger.error("Error while saving uploaded sql file!", e);
    }
  }

}
