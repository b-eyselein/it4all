package model.mindmap.evaluation;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.mindmap.basics.TreeNode;
import model.mindmap.evaluation.enums.EvalParserType;
import model.mindmap.evaluation.enums.MetaDataState;
import model.mindmap.evaluation.enums.Modus;
import model.mindmap.parser.AbstractEvaluationParser;

public class MultipleRoots {

  // will be set from outside if gui program
  private MetaDataState mds = null;
  private Modus modus = null;

  public void createMetaFromMap(String parserType, String solutionMapPath, String metaToCreatePath) throws Exception {
    RWExcel rwe = new RWExcel();
    AbstractEvaluationParser abstractEvaluationParser = EvalParserType.valueOf(parserType).getParser();
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
    AbstractEvaluationParser abstractEvaluationParser = EvalParserType.valueOf(parserType).getParser();
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
