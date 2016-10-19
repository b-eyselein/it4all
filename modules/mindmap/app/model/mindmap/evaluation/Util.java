package model.mindmap.evaluation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.mindmap.basics.TreeNode;
import model.mindmap.evaluation.enums.DifferenceResult;

public class Util {
  
  private int treeDepth = 0;
  
  /**
   * Copies the internal state of a TreeNode created from the solution to the
   * corresponding TreeNode from the user input. This can only happen if the
   * TreeNodes can be matched. Also if the result of a node is partially
   * correct, the percentage difference between the solution and the input node
   * is calculated.
   *
   * @param solutionRoots
   *          List of root nodes from the solution file
   * @param inputRoots
   *          list of root nodes from the input file
   */
  public static void applyMetaDataFromSolutionToInput(List<TreeNode> solutionRoots, List<TreeNode> inputRoots) {
    for(TreeNode root: solutionRoots) {
      // propagate optional from parent to children if true
      setOptionalAndMarkAsSolution(root);
      checkIfRealRoot(root);
    }
    // this is only correct after all children are set optional
    for(TreeNode root: solutionRoots) {
      // find solution node in input nodes and set its meta data values
      traverseTreeAndSetMetaData(root, inputRoots);
    }
  }
  
  /**
   * Adds to every node in the trees their respective state depending on the
   * results of @see {@link #getDifferenceTupel(HashMap, HashMap)}}
   *
   * @param rootList
   *          List of roots
   * @param result
   *          assignment of tuples to missing/wrong/partially correct
   */
  public static void applyResultToNodes(List<TreeNode> rootList, TupelDifferenceContainer result) {
    for(TreeNode root: rootList) {
      List<Tuple> missingTuple = result.getMapMissing().get(root.getText());
      List<Tuple> wrongTuple = result.getMapWrong().get(root.getText());
      List<Tuple> partiallyCorrectTuple = result.getMapPartiallyCorrect().get(root.getText());
      traverseTreeAndSetResults(root, missingTuple, wrongTuple, partiallyCorrectTuple);
    }
  }
  
  /**
   * This method creates parent-child tuple for all trees. Also all tuples
   * corresponding to the same tree are put in a HashMap. Key is the text of the
   * root. NOTE: as in the definition of TreeNode demanded, the text of a
   * TreeNode is unique A / \ B C / \ D E Tuples are: (A,B), (A,C), (B,D), (B,E)
   *
   * @param listOfRoots
   *          roots of trees
   * @return HashMap which contains all tuples of a tree for all trees. Key is
   *         the text of the root of the tree.
   */
  public static Map<String, List<Tuple>> buildTuples(List<TreeNode> listOfRoots) {
    Map<String, List<Tuple>> map = new HashMap<>();
    for(TreeNode root: listOfRoots) {
      List<Tuple> listOfTuple = new LinkedList<>();
      buildTuple(listOfTuple, root);
      map.put(root.getText(), listOfTuple);
    }
    return map;
  }
  
  /**
   * Checks for all nodes of the user input if it is possible to give some
   * feedback for nodes which are placed wrong by the user.
   *
   * @param solutionRoots
   *          roots of solution map
   * @param inputRoots
   *          roots of user input map
   */
  public static void checkIfFeedbackPossible(List<TreeNode> solutionRoots, List<TreeNode> inputRoots) {
    for(TreeNode rootInput: inputRoots) {
      traverseForFeedback(solutionRoots, rootInput);
    }
  }
  
  /**
   * This method takes all tuples of one tree of the solution file and checks if
   * the corresponding tree of the user input file contains these tuples. For
   * each tuple possible outcomes are: - The user input tree contains the tuple:
   * everything is correct - The user input tree does not contain the tuple:
   * tuple is saved as missing - The user input tree does contain a tuple but
   * the text of the nodes of the tuple is slightly wrong: tuple is saved as
   * partially correct - The user input contains a tuple which is not in the
   * solution: tuple is saved as wrong This is done for all trees.
   *
   * @param solutions
   *          Map of all trees with root.text as keys and all tuples of the
   *          specific tree
   * @param input
   *          Map of all trees with root.text as keys and all tuples of the
   *          specific tree
   * @return Container class which holds all wrong/missing/partially correct
   *         tuples for each tree
   */
  public static TupelDifferenceContainer getDifferenceTupel(Map<String, List<Tuple>> solutions,
      Map<String, List<Tuple>> input) {
    Map<String, List<Tuple>> mapMissingTuple = new HashMap<>();
    Map<String, List<Tuple>> mapWrongTuple = new HashMap<>();
    Map<String, List<Tuple>> mapPartiallyCorrect = new HashMap<>();
    for(Map.Entry<String, List<Tuple>> entry: solutions.entrySet()) {
      // tmpList contains all tuple which differ from the exact solution but are
      // fine by the definition of synonyms
      // this is a list because of patterns like .*
      String key = entry.getKey();
      List<Tuple> solutionValues = entry.getValue();
      // all roots in solution file MUST exist as roots in received input file
      // TODO allow this to happen
      // Problem: if root is different i don't know which solution tree maps to
      // the input tree
      List<Tuple> inputValues = input.get(key);
      List<Tuple> missing = new LinkedList<>();
      List<Tuple> existing = new LinkedList<>();
      List<Tuple> partiallyCorrectList = new LinkedList<>();
      for(Tuple solTupel: solutionValues) {
        boolean contains = false;
        boolean partiallyCorrect = false;
        List<Tuple> tmpList = new LinkedList<>();
        // check for identity and synonyms identity including regular
        // expressions
        for(Tuple inTupel: inputValues) {
          if(isSameTupel(solTupel, inTupel)) {
            contains = true;
            tmpList.add(inTupel);
          }
        }
        // if there is no identity/regex match: check for edit-distance
        if(!contains) {
          for(Tuple inTupel: inputValues) {
            if(isPartiallyCorrect(solTupel, inTupel)) {
              partiallyCorrect = true;
              tmpList.add(inTupel);
            }
          }
        }
        if(!contains && !partiallyCorrect) {
          missing.add(solTupel);
        } else {
          addDivergentSolutions(solTupel, tmpList, existing);
          if(partiallyCorrect) {
            addDivergentSolutions(solTupel, tmpList, partiallyCorrectList);
          }
        }
      }
      mapMissingTuple.put(key, missing);
      inputValues.removeAll(existing);
      mapWrongTuple.put(key, inputValues);
      mapPartiallyCorrect.put(key, partiallyCorrectList);
    }
    return new TupelDifferenceContainer(mapMissingTuple, mapWrongTuple, mapPartiallyCorrect);
  }
  
  /**
   * Calculate the sum of all points of nodes inside all trees.
   *
   * @param rootList
   *          roots of the trees.
   * @return Container class which holds the result and the maximal points
   */
  public static PointsResult getOverallPoints(List<TreeNode> rootList) {
    PointsResult pr = new PointsResult();
    for(TreeNode root: rootList) {
      PointsResult prTree = getSingleTreePoints(root);
      pr.addToMaxPoints(prTree.getMaxPoints());
      pr.addToRealPoints(prTree.getRealPoints());
    }
    return pr;
  }
  
  /**
   * Calculate the sum of all points of nodes inside a single tree.
   *
   * @param root
   *          root of the tree
   * @return sum of the the points in the tree
   */
  public static PointsResult getSingleTreePoints(TreeNode root) {
    PointsResult pr = new PointsResult();
    traverseTreeAddPoints(root, pr);
    return pr;
  }
  
  /**
   * Inserts a dummy node as root node of all roots of the given list. This
   * makes one tree out of many. This is used for TOC.
   *
   * @param rootList
   *          roots of trees
   * @return list of roots but only with one root
   */
  public static List<TreeNode> mergeTrees(List<TreeNode> rootList) {
    List<TreeNode> noRoots = new LinkedList<>();
    for(TreeNode root: rootList) {
      if(root.isMetaNode()) {
        noRoots.add(root);
      }
    }
    rootList.removeAll(noRoots);
    if(rootList.size() > 1) {
      TreeNode dummy = new TreeNode("dummy");
      dummy.setText("dummyText");
      for(TreeNode root: rootList) {
        root.setParent(dummy);
        dummy.addChild(root);
      }
      List<TreeNode> onlyOneRoot = new LinkedList<>();
      onlyOneRoot.add(dummy);
      return onlyOneRoot;
    }
    return rootList;
  }
  
  /**
   * calculate the achieved points depending on the the results of
   *
   * @see {@link #getDifferenceTupel(HashMap, HashMap)}}. Currently wrong nodes
   *      are rated with zero points and for partially correct nodes the rating
   *      is based on the percentage difference
   * @see {@link #applyMetaDataFromSolutionToInput(LinkedList, LinkedList)}} and
   *      the maximal points.
   *
   * @param rootList
   */
  public static void setRealPoints(List<TreeNode> rootList) {
    for(TreeNode root: rootList) {
      traverseTreeAndSetRealPoints(root);
    }
  }
  
  private static void addDivergentSolutions(Tuple solTupel, List<Tuple> listToCheck, List<Tuple> resultingList) {
    // must be done in any chase
    resultingList.add(solTupel);
    // add the correct but from solution different answer to the existing ones
    // this has impact later on when the excel file is written
    for(int i = 0; i < listToCheck.size(); i++) {
      Tuple tmp = listToCheck.get(i);
      if(!solTupel.getFirstNode().getText().equals(tmp.getFirstNode().getText())
          || !solTupel.getSecondNode().getText().equals(tmp.getSecondNode().getText())) {
        resultingList.add(tmp);
      }
    }
  }
  
  private static void buildTuple(List<Tuple> listOfTupel, TreeNode node) {
    for(TreeNode child: node.getChildren()) {
      listOfTupel.add(new Tuple(node, child));
      buildTuple(listOfTupel, child);
    }
  }
  
  private static void checkIfApplyMetaData(TreeNode solNode, TreeNode inNode) {
    if(isSameNode(solNode, inNode)) {
      inNode.setOptional(solNode.isOptional());
      inNode.setRealRating(solNode.getRealRating());
      inNode.setMaxRating(solNode.getMaxRating());
      inNode.setSynonyms(solNode.getSynonyms());
      if(solNode.isMetaNode()) {
        inNode.setMetaNode(true);
      }
    } else if(Levenshtein.isPartiallyCorrect(solNode, inNode)) {
      inNode.setOptional(solNode.isOptional());
      inNode.setRealRating(solNode.getRealRating());
      inNode.setMaxRating(solNode.getMaxRating());
    }
    for(TreeNode inChild: inNode.getChildren()) {
      checkIfApplyMetaData(solNode, inChild);
    }
  }
  
  private static void checkIfRealRoot(TreeNode treeNode) {
    if(treeNode.getText().startsWith("Arbeitsauftrag")) {
      treeNode.setMetaNode(true);
    }
  }
  
  private static boolean isPartiallyCorrect(Tuple solTupel, Tuple inTupel) {
    boolean firstPartiallyCorrect = Levenshtein.isPartiallyCorrect(solTupel.getFirstNode(), inTupel.getFirstNode());
    boolean secondPartiallyCorrect = Levenshtein.isPartiallyCorrect(solTupel.getSecondNode(), inTupel.getSecondNode());
    boolean firstNodesEqual = isSameNode(solTupel.getFirstNode(), inTupel.getFirstNode());
    boolean secondNodesEqual = isSameNode(solTupel.getSecondNode(), inTupel.getSecondNode());
    // if both are partially correct or one is partially and one completely
    // correct (identity and regex) return true
    // both completely correct is already impossible because this case was
    // already handled
    if((firstPartiallyCorrect && secondPartiallyCorrect) || (firstNodesEqual && secondPartiallyCorrect)
        || (firstPartiallyCorrect && secondNodesEqual)) {
      if(firstPartiallyCorrect) {
        inTupel.getFirstNode().setCorrectSolutions(solTupel.getFirstNode().getSynonyms());
      }
      if(secondPartiallyCorrect) {
        inTupel.getSecondNode().setCorrectSolutions(solTupel.getSecondNode().getSynonyms());
      }
      return true;
    }
    return false;
  }
  
  // checks for identity of synonyms and regex
  private static boolean isSameNode(TreeNode solNode, TreeNode inNode) {
    boolean isEqual = false;
    for(String synonym: solNode.getSynonyms()) {
      if(inNode.getText().matches(synonym)) {
        isEqual = true;
      }
    }
    return isEqual;
  }
  
  // checks for identity of synonyms and regex for both nodes of the tuple
  private static boolean isSameTupel(Tuple solutionTupel, Tuple inputTupel) {
    boolean firstEqual = isSameNode(solutionTupel.getFirstNode(), inputTupel.getFirstNode());
    boolean secondEqual = isSameNode(solutionTupel.getSecondNode(), inputTupel.getSecondNode());
    if(firstEqual && secondEqual) {
      return true;
    }
    return false;
  }
  
  private static void setOptionalAndMarkAsSolution(TreeNode treeNode) {
    // make not optional if his parent was optional
    if(treeNode.getParent() != null && treeNode.getParent().isOptional()) {
      treeNode.setOptional(true);
    }
    // this node is a solution node - important for later distinction
    treeNode.setSolution(true);
    for(TreeNode child: treeNode.getChildren()) {
      setOptionalAndMarkAsSolution(child);
    }
  }
  
  private static void traverseAndCheckText(TreeNode treeNode, TreeNode inputNode) {
    if(isSameNode(treeNode, inputNode)) {
      String intro = "This node should be here: ";
      String path = " --- " + treeNode.getText();
      TreeNode currentNode = treeNode;
      while(currentNode.getParent() != null) {
        currentNode = currentNode.getParent();
        if(currentNode.getParent() == null) {
          path = currentNode.getText() + path;
        } else {
          path = " --- " + currentNode.getText() + path;
        }
      }
      inputNode.addCorrectSolution(intro + path);
    }
    for(TreeNode child: treeNode.getChildren()) {
      traverseAndCheckText(child, inputNode);
    }
  }
  
  private static void traverseForFeedback(List<TreeNode> solutionRoots, TreeNode inputNode) {
    if(inputNode.getDifferenceResult() == DifferenceResult.WRONG) {
      for(TreeNode root: solutionRoots) {
        traverseAndCheckText(root, inputNode);
      }
    }
    for(TreeNode child: inputNode.getChildren()) {
      traverseForFeedback(solutionRoots, child);
    }
  }
  
  private static void traverseTreeAddPoints(TreeNode treeNode, PointsResult pointsResult) {
    pointsResult.addToMaxPoints(treeNode.getMaxRating());
    pointsResult.addToRealPoints(treeNode.getRealRating());
    for(TreeNode child: treeNode.getChildren()) {
      traverseTreeAddPoints(child, pointsResult);
    }
  }
  
  private static void traverseTreeAndSetMetaData(TreeNode treeNode, List<TreeNode> inputRoots) {
    // find solution node in input if it exists and give same meta status
    for(TreeNode rootInput: inputRoots) {
      checkIfApplyMetaData(treeNode, rootInput);
    }
    for(TreeNode child: treeNode.getChildren()) {
      traverseTreeAndSetMetaData(child, inputRoots);
    }
  }
  
  private static void traverseTreeAndSetRealPoints(TreeNode treeNode) {
    if(treeNode.getDifferenceResult() == DifferenceResult.CORRECT) {
      treeNode.setRealRating(treeNode.getMaxRating());
    } else if(treeNode.getDifferenceResult() == DifferenceResult.PARTIALLY_CORRECT) {
      treeNode.setRealRating(treeNode.getDistancePercent() * treeNode.getMaxRating());
    }
    for(TreeNode child: treeNode.getChildren()) {
      traverseTreeAndSetRealPoints(child);
    }
  }
  
  private static void traverseTreeAndSetResults(TreeNode treeNode, List<Tuple> missingTuple, List<Tuple> wrongTuple,
      List<Tuple> partiallyCorrectTuple) {
    if(treeNode.getParent() == null) {
      // root nodes of tree can either be wrong or okay
      if(missingTuple == null) {
        treeNode.setDifferenceResult(DifferenceResult.WRONG);
      } else {
        treeNode.setDifferenceResult(DifferenceResult.CORRECT);
      }
    } else {
      Tuple tuple = new Tuple(treeNode.getParent(), treeNode);
      if(missingTuple.contains(tuple)) {
        treeNode.setDifferenceResult(DifferenceResult.MISSING);
      } else if(wrongTuple.contains(tuple)) {
        treeNode.setDifferenceResult(DifferenceResult.WRONG);
      } else if(partiallyCorrectTuple.contains(tuple)) {
        // parent is partially correct but child is 100% correct
        if(treeNode.getDistancePercent() == 1.0) {
          treeNode.setDifferenceResult(DifferenceResult.CORRECT);
        } else {
          treeNode.setDifferenceResult(DifferenceResult.PARTIALLY_CORRECT);
        }
      } else {
        treeNode.setDifferenceResult(DifferenceResult.CORRECT);
      }
    }
    for(TreeNode child: treeNode.getChildren()) {
      traverseTreeAndSetResults(child, missingTuple, wrongTuple, partiallyCorrectTuple);
    }
  }
  
  public int getMaxDepthOfTrees(List<TreeNode> rootList) {
    int maxDepth = 0;
    for(TreeNode root: rootList) {
      treeDepth = -1;
      traverseTreeForMaxDepth(root, 0);
      if(treeDepth > maxDepth) {
        maxDepth = treeDepth;
      }
    }
    return maxDepth;
  }
  
  private void traverseTreeForMaxDepth(TreeNode treeNode, int currentDepth) {
    if(currentDepth > treeDepth) {
      treeDepth = currentDepth;
    }
    treeNode.setDepth(currentDepth);
    for(TreeNode child: treeNode.getChildren()) {
      traverseTreeForMaxDepth(child, currentDepth + 1);
    }
  }
}
