package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import model.AdminSecured;
import model.Util;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated(AdminSecured.class)
public abstract class AdminController extends Controller {

  protected static final String BODY_FILE_NAME = "file";
  protected static final String ADMIN_FOLDER = "admin";

  private static final String BASE_DIR = "conf/resources";

  private static final String EX_FILE_NAME = "exercises.json";
  private static final String EX_SCHEMA_FILE_NAME = "exerciseSchema.json";

  protected String exerciseType;

  protected Path jsonFile;
  protected Path jsonSchemaFile;

  protected Util util;

  public AdminController(Util theUtil, String theExerciseType) {
    util = theUtil;
    exerciseType = theExerciseType;
    jsonFile = Paths.get(BASE_DIR, exerciseType, EX_FILE_NAME);
    jsonSchemaFile = Paths.get(BASE_DIR, exerciseType, EX_SCHEMA_FILE_NAME);
  }

  protected static void saveUploadedFile(Path savingDir, Path pathToUploadedFile, Path saveTo) {
    try {
      if(!savingDir.toFile().exists() && !savingDir.toFile().isDirectory())
        Files.createDirectories(savingDir);
      Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      Logger.error("Error while saving uploaded sql file!", e);
    }
  }

  public abstract Result create();

  public abstract Result uploadFile();

  public abstract Result uploadForm();
}
