package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;

public abstract class StartUpChecker {

  protected static Logger.ALogger theLogger = Logger.of("startup");

  public static JsonNode readJsonFile(Path path) {
    try {
      String fileAsString = String.join("\n", Files.readAllLines(path));
      return Json.parse(fileAsString);
    } catch (IOException e) {
      theLogger.error("Error while reading file " + path, e);
    }
    return null;

  }

  public StartUpChecker() {
    performStartUpCheck();
  }

  protected abstract void performStartUpCheck();

}
