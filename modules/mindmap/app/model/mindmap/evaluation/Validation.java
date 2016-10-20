package model.mindmap.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import model.mindmap.basics.TreeNode;
import model.mindmap.parser.MindManagerParser;
import model.mindmap.parser.ParserFactory;

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
  public static boolean checkForMeta(String solutionPath, String metaFilePath) throws Exception {
    if(new File(metaFilePath).exists()) {
      return true;
    } else {
      List<TreeNode> solutionRoots = (ParserFactory.getEvaluationParser("MINDMANAGER")).read(new File(solutionPath));
      Util.applyMetaDataFromSolutionToInput(solutionRoots, new LinkedList<>());
      (new RWExcel()).createEmptyMetaFile(metaFilePath, solutionRoots);
      return false;
    }
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
  public static boolean checkForMeta(String parserType, String solutionPath, String metaFilePath) throws Exception {
    if(new File(metaFilePath).exists()) {
      return true;
    } else {
      List<TreeNode> solutionRoots = (ParserFactory.getEvaluationParser(parserType)).read(new File(solutionPath));
      Util.applyMetaDataFromSolutionToInput(solutionRoots, new LinkedList<>());
      (new RWExcel()).createEmptyMetaFile(metaFilePath, solutionRoots);
      return false;
    }
  }
  
  /**
   * Validates the meta file which is given as file path. Even so this
   * validation was true, there might occur errors at evaluation.
   *
   * @param metaPath
   *          file path to meta file
   * @return true if validation was correct, else false
   */
  public static boolean validateMeta(String metaPath) {
    RWExcel rwe = new RWExcel();
    boolean valid = false;
    try {
      valid = rwe.validate(metaPath);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return valid;
  }
  
  /**
   * Validates the mindmap which is given as file path. Even so this validation
   * was true, there might occur errors at evaluation.
   *
   * @param mapPath
   *          file path to mindmap
   * @return true if validation was correct, else false
   */
  public static boolean validateMindMap(String mapPath) {
    MindManagerParser mmp = new MindManagerParser();
    try {
      mmp.read(new File(mapPath));
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
