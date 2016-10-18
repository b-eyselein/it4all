package model.mindmap.evaluation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.mindmap.basics.TreeNode;
import model.mindmap.evaluation.enums.MetaDataState;
import model.mindmap.evaluation.enums.Modus;
import model.mindmap.parser.AbstractEvaluationParser;
import model.mindmap.parser.ParserFactory;

public class MultipleRoots {
  
  // will be set from outside if gui program
  private MetaDataState mds = null;
  private Modus modus = null;
  
  public void createMetaFromMap(String parserType, String solutionMapPath, String metaToCreatePath) throws Exception {
    RWExcel rwe = new RWExcel();
    AbstractEvaluationParser abstractEvaluationParser = ParserFactory.getEvaluationParser(parserType);
    List<TreeNode> solutionRoots = abstractEvaluationParser.read(new File(solutionMapPath));
    Util.applyMetaDataFromSolutionToInput(solutionRoots, new LinkedList<>());
    rwe.handleMetaData(metaToCreatePath, solutionRoots, MetaDataState.FROM_MINDMAP);
  }
  
  public void evalMultiRootMapWithExcel(String parserType, String input, String solution, String result,
      String alteredSolution, String alteredInput, String metaData, String template) throws Exception {
    RWExcel rwe = new RWExcel();
    // must be uncommented to be a console program - else mds and modus are set
    // from outside
    // mds = askUserForMetaDataState(metaData);
    // modus = askUserForModus();
    AbstractEvaluationParser abstractEvaluationParser = ParserFactory.getEvaluationParser(parserType);
    abstractEvaluationParser.setModus(modus);
    List<TreeNode> inputRoots = abstractEvaluationParser.read(new File(input));
    List<TreeNode> solutionRoots = abstractEvaluationParser.read(new File(solution));
    // this must be called before handleMetaData()... //else there might be
    // redundant 'yes' in the meta file. (example: press = 1, Haustier = yes,
    // Dackel = yes)
    Util.applyMetaDataFromSolutionToInput(solutionRoots, inputRoots);
    rwe.handleMetaData(metaData, solutionRoots, mds);
    // ...and after. //this is a must
    Util.applyMetaDataFromSolutionToInput(solutionRoots, inputRoots);
    Map<String, List<Tuple>> mapInput = Util.buildTuples(inputRoots);
    Map<String, List<Tuple>> mapSolutions = Util.buildTuples(solutionRoots);
    TupelDifferenceContainer resultDifferences = Util.getDifferenceTupel(mapSolutions, mapInput);
    Util.applyResultToNodes(solutionRoots, resultDifferences);
    Util.applyResultToNodes(inputRoots, resultDifferences);
    Util.checkIfFeedbackPossible(solutionRoots, inputRoots);
    Util.setRealPoints(solutionRoots);
    Util.setRealPoints(inputRoots);
    rwe.writeEvaluation(result, inputRoots, solutionRoots);
    abstractEvaluationParser.setPathsForAlteredOutputs(result, metaData, alteredInput, alteredSolution);
    abstractEvaluationParser.write(alteredSolution, solutionRoots, template);
    abstractEvaluationParser.write(alteredInput, inputRoots, template);
  }
  
  // private MetaDataState askUserForMetaDataState(String metaFilePath) {
  // File metaFile = new File(metaFilePath);
  // System.out.println("Press 1: Read from MindMap (reminder: for this your
  // have to change the solution path; see main() method)");
  // System.out.println("Press 2: Create empty Metafile");
  // if(metaFile.exists() && !metaFile.isDirectory()) {
  // System.out.println("Press 3: Read from Metafile");
  // }
  // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  // int in = -1;
  // try {
  // in = Integer.parseInt(br.readLine());
  // } catch (NumberFormatException | IOException e) {
  // e.printStackTrace();
  // }
  // switch(in) {
  // case 1:
  // return MetaDataState.FROM_MINDMAP;
  // case 2:
  // return MetaDataState.EMPTY;
  // case 3:
  // //currently it is the standard solution file from which is read
  // return MetaDataState.FROM_METAFILE;
  // default:
  // System.out.println("Wrong input. Restart.");
  // return null;
  // }
  // }
  
  // private Modus askUserForModus() {
  // System.out.println("Press 1: Minimal modus (only colored results and
  // points)");
  // System.out.println("Press 2: Maximal modus (all available informations are
  // displayed)");
  // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  // int in = -1;
  // try {
  // in = Integer.parseInt(br.readLine());
  // } catch (NumberFormatException | IOException e) {
  // e.printStackTrace();
  // }
  // switch(in) {
  // case 1:
  // return Modus.MINIMAL;
  // case 2:
  // return Modus.MAXIMAL;
  // default:
  // System.out.println("wrong input. Restart.");
  // return null;
  // }
  // }
  
  public MetaDataState getMetaDataState() {
    return mds;
  }
  
  public Modus getModus() {
    return modus;
  }
  
  public void setMetaDataState(MetaDataState metaDataState) {
    this.mds = metaDataState;
  }
  
  public void setModus(Modus modus) {
    this.modus = modus;
  }
}
