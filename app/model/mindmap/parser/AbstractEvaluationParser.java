package model.mindmap.parser;

import java.nio.file.Path;

import model.mindmap.evaluation.Properties;
import model.mindmap.evaluation.enums.Modus;

public abstract class AbstractEvaluationParser implements AbstractParser {

  private boolean multipleOutput = false;

  private Path result;
  private Path metaData;
  private Path alteredInput;
  private Path alteredSolution;

  private Modus modus = null;

  public Path getAlteredInput() {
    return alteredInput;
  }

  public Path getAlteredSolution() {
    return alteredSolution;
  }

  public Path getMetaData() {
    return metaData;
  }

  public Modus getModus() {
    return modus;
  }

  public Path getResult() {
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
  public void setPathsForAlteredOutputs(Path result, Path metaData, Path alteredInput, Path alteredSolution) {
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
