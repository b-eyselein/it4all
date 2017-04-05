package controllers.sql;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import controllers.core.AbstractAdminController;
import model.SqlExerciseReader;
import model.Util;
import model.exercise.SqlExercise;
import model.exercise.SqlScenario;
import play.data.FormFactory;
import play.db.Database;
import play.db.NamedDatabase;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class SQLAdmin extends AbstractAdminController<SqlExercise, SqlExerciseReader> {

  private Database sqlSelect;

  private Database sqlOther;

  @Inject
  public SQLAdmin(Util theUtil, FormFactory theFactory, @NamedDatabase("sqlselectroot") Database theSqlSelect,
      @NamedDatabase("sqlotherroot") Database theSqlOther) {
    super(theUtil, theFactory, "sql", new SqlExerciseReader());
    sqlSelect = theSqlSelect;
    sqlOther = theSqlOther;
  }

  @Override
  public Result readStandardExercises() {
    List<SqlScenario> results = exerciseReader.readStandardScenarioes();
    saveScenarioes(results);
    return ok(views.html.preview.render(getUser(), views.html.sqlcreation.render(results)));
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

    List<SqlScenario> results = exerciseReader.readScenarioes(saveTo);

    saveScenarioes(results);

    return ok(views.html.preview.render(getUser(), views.html.sqlcreation.render(results)));
  }

  @Override
  public Result uploadForm() {
    return ok(views.html.sqlupload.render(getUser()));
  }

  private void saveScenarioes(List<SqlScenario> results) {
    for(SqlScenario result: results) {
      result.save();
      // FIXME: run script to create database
      exerciseReader.runCreateScript(sqlSelect, result);
      exerciseReader.runCreateScript(sqlOther, result);
      saveExercises(result.exercises);
    }
  }

  @Override
  protected void saveExercises(List<SqlExercise> exercises) {
    for(SqlExercise ex: exercises)
      ex.save();
  }

}
