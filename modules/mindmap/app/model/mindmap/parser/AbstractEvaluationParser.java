package model.mindmap.parser;

import java.io.File;

import model.mindmap.evaluation.Properties;
import model.mindmap.evaluation.enums.Modus;

public abstract class AbstractEvaluationParser extends AbstractParser {

  private boolean multipleOutput = false;

  private String result;
  private String metaData;
  private String alteredInput;
  private String alteredSolution;

  private Modus modus = null;

  public String getAbsolutePath(String relativePath) {
    File file = new File(relativePath);
    return file.getAbsolutePath();
  }

  public String getAlteredInput() {
    return alteredInput;
  }

  public String getAlteredSolution() {
    return alteredSolution;
  }

  public String getMetaData() {
    return metaData;
  }

  public Modus getModus() {
    return modus;
  }

  public String getResult() {
    return result;
  }

  public boolean isMultipleOutput() {
    return multipleOutput;
  }

  public void setModus(Modus modus) {
    this.modus = modus;
  }

  /**
   * With this method it is possible to set file paths from outside. This is
   * necessary because the paths are needed to create a mindmap.
   *
   * @param result
   *          file path to the saving location of the result.xls
   * @param metaData
   *          file path to the meta file
   * @param alteredInput
   *          file path to the saving location of the altered input mindmap
   * @param alteredSolution
   *          file path to the saving location of the altered input mindmap
   */
  public void setPathsForAlteredOutputs(String result, String metaData, String alteredInput, String alteredSolution) {
    this.result = result;
    this.metaData = metaData;
    this.alteredInput = alteredInput;
    this.alteredSolution = alteredSolution;
    multipleOutput = true;
  }

  /**
   * Properties change the content of the mindmap.
   *
   * @param properties
   */
  public void setProperties(Properties properties) {
    if(properties.getModus() != null) {
      modus = properties.getModus();
    }
  }
}
