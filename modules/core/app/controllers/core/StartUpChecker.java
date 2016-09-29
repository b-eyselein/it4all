package controllers.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.libs.Json;

public abstract class StartUpChecker {
  
  protected static Logger.ALogger theLogger = Logger.of("startup");
  
  public static JsonNode readJsonFile(Path path) throws IOException {
    return Json.parse(String.join("\n", Files.readAllLines(path)));
  }
  
  public StartUpChecker() {
    performStartUpCheck();
  }
  
  protected abstract void performStartUpCheck();
  
}
