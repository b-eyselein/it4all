package model.mindmap.evaluation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import model.mindmap.basics.TreeNode;
import model.mindmap.evaluation.enums.EvalParserType;
import model.mindmap.parser.MindManagerParser;
import play.Logger;

public class Validation {

  private Validation() {

  }

  /**
   * The MindManager parser is used. Else @see
   * {@link #checkForMeta(String, String, String)}}
   *
   * @param solutionPath
   * @param metaFilePath
   * @return
   * @throws Exception
   */
  public static boolean checkForMeta(Path solutionPath, Path metaFilePath) throws IOException, ParsingException {
    if(metaFilePath.toFile().exists())
      return true;

    List<TreeNode> solutionRoots = EvalParserType.MINDMANAGER.getParser().read(solutionPath.toFile());
    Util.applyMetaDataFromSolutionToInput(solutionRoots, new LinkedList<>());
    (new RWExcel()).createEmptyMetaFile(metaFilePath, solutionRoots);
    return false;
  }

  /**
   * This method checks if there is a existing meta file. If this is the case
   * the method returns true. If not, a new meta file will be created and the
   * method returns false. To build the new meta file, the solution file is
   * needed.
   *
   * @param parserType
   *          declares which parser will be used
   * @param solutionPath
   *          file path to the perfect solution mindmap
   * @param metaFilePath
   *          file path where the meta file is or should be
   * @return true if the meta file does already exist. Else false and a new meta
   *         file is created.
   * @throws Exception
   */
  public static boolean checkForMeta(String parserType, Path solutionPath, Path metaFilePath) throws Exception {
    if(metaFilePath.toFile().exists())
      return true;

    List<TreeNode> solutionRoots = EvalParserType.valueOf(parserType).getParser().read(solutionPath.toFile());
    Util.applyMetaDataFromSolutionToInput(solutionRoots, new LinkedList<>());
    (new RWExcel()).createEmptyMetaFile(metaFilePath, solutionRoots);
    return false;
  }

  /**
   * Validates the meta file which is given as file path. Even so this
   * validation was true, there might occur errors at evaluation.
   *
   * @param metaPath
   *          file path to meta file
   * @return true if validation was correct, else false
   */
  public static boolean validateMeta(Path metaPath) {
    try {
      return new RWExcel().validate(metaPath);
    } catch (IOException e) {
      Logger.error("Error while validating meta data", e);
      return false;
    }
  }

  /**
   * Validates the mindmap which is given as file path. Even so this validation
   * was true, there might occur errors at evaluation.
   *
   * @param mapPath
   *          file path to mindmap
   * @return true if validation was correct, else false
   */
  public static boolean validateMindMap(Path mapPath) {
    try {
      new MindManagerParser().read(mapPath.toFile());
      return true;
    } catch (ParsingException e) {
      Logger.error("Error while validating mind map", e);
      return false;
    }
  }
}
