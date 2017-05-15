package model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import model.exercise.Exercise;
import play.Logger;

@Entity
public class XmlExercise extends Exercise {

  private static final String EX_TYPE = "xml";

  public static final com.avaje.ebean.Model.Finder<Integer, XmlExercise> finder = new com.avaje.ebean.Model.Finder<>(
      XmlExercise.class);

  @Column(columnDefinition = "text")
  public String fixedStart;

  @Enumerated(EnumType.STRING)
  public XmlExType exerciseType;

  public String referenceFileName; // NOSONAR

  public XmlExercise(int theId) {
    super(theId);
  }

  public String getGrammarFileEnding() {
    return exerciseType.getGramFileEnding();
  }

  public String getReferenceCode(Util util) {
    Path referenceFilePath = getRefFilePath(util);

    try {
      if(referenceFilePath.toFile().exists())
        return Util.readFileFromPath(referenceFilePath);

    } catch (IOException e) {
      Logger.error("There has been an error reading the file", e);
    }
    return "FEHLER!";
  }

  public String getReferenceFileEnding() {
    return exerciseType.getRefFileEnding();
  }

  public String getStudentFileEnding() {
    return exerciseType.getStudFileEnding();
  }

  public String getTag() {
    return exerciseType.getTag();
  }

  @Override
  public void saveInDB() {
    save();
    // exerciseReader.checkOrCreateSampleFile(util, ex);
  }

  private Path getRefFilePath(Util util) {
    // @formatter:off
    return Paths
        .get(util.getSampleFileForExercise(EX_TYPE, referenceFileName).toString() + "." + getReferenceFileEnding());
    // @formatter:on
  }

}