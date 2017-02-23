package model;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

public class JsonValidator {

  public static String correctExercise(File jsonFile, int exerciseId) throws ProcessingException, IOException {
    File schemaFile = searchSchemaToExercise(jsonFile, exerciseId);
    if(!validateJson(schemaFile, jsonFile)) {
      return "Invalides JSON";
    }
    return "";
  }

  public static void main(String[] args) throws ProcessingException, IOException {
    // FIXME: move to JUnit-Test with relative path, commit files!
    File schemaFile = new File("/home/kfu/Desktop/schema.json");
    File jsonFile = new File("/home/kfu/Desktop/jsoninput.json");
    Boolean validJson = validateJson(schemaFile, jsonFile);
    System.out.println("Valides JSON-Format und valid gegen Schema: " + validJson);
  }

  public static File searchSchemaToExercise(File jsonFile, int exerciseId) throws ProcessingException, IOException {
    File schemaFile = null;
    switch(exerciseId) {
    case 0:
      return schemaFile = new File("/home/kfu/Desktop/schema.json");
    case 1:
      return schemaFile = new File("/home/kfu/Desktop/schema2.json");
    default:
      return schemaFile;
    }
  }

  public static Boolean validateJson(File schemaFile, File jsonFile) throws ProcessingException, IOException {
    try {
      if(ValidationUtils.isJsonValid(schemaFile, jsonFile)) {
        // System.out.println("Valid!");
        return true;
      } else {
        // System.out.println("NOT valid vs Schema!");
        return false;
      }
    } catch (JsonParseException e) {
      System.out.println("Invalides JSON-FORMAT");
    }
    return false;
  }
}
