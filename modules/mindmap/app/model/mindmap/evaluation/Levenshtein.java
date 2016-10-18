package model.mindmap.evaluation;

import model.mindmap.basics.TreeNode;

public class Levenshtein {
  
  // TODO there should not be a limit in this class - the evaluation should be
  // made somewhere else
  private static final double MINIMAL_PERCENT = 0.8;

  // FIXME: use implementation is core if possible!
  
  public static boolean isPartiallyCorrect(TreeNode solNode, TreeNode inNode) {
    double maxCorrectness = -1.0;
    for(String editSyn: solNode.getEditSynonyms()) {
      int maxLength = 0;
      if(editSyn.length() > inNode.getText().length()) {
        maxLength = editSyn.length();
      } else {
        maxLength = inNode.getText().length();
      }
      int distance = LevenshteinDistance(editSyn, inNode.getText());
      double correctPercentage = 1.0 - ((double) distance / (double) maxLength);
      if(correctPercentage > maxCorrectness) {
        maxCorrectness = correctPercentage;
      }
    }
    if(maxCorrectness >= MINIMAL_PERCENT) {
      solNode.setDistancePercent(maxCorrectness);
      inNode.setDistancePercent(maxCorrectness);
      return true;
    }
    return false;
  }
  
  // see
  // https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
  public static int LevenshteinDistance(CharSequence lhs, CharSequence rhs) {
    int len0 = lhs.length() + 1;
    int len1 = rhs.length() + 1;
    
    // the array of distances
    int[] cost = new int[len0];
    int[] newcost = new int[len0];
    
    // initial cost of skipping prefix in String s0
    for(int i = 0; i < len0; i++)
      cost[i] = i;
    
    // dynamically computing the array of distances
    
    // transformation cost for each letter in s1
    for(int j = 1; j < len1; j++) {
      // initial cost of skipping prefix in String s1
      newcost[0] = j;
      
      // transformation cost for each letter in s0
      for(int i = 1; i < len0; i++) {
        // matching current letters in both strings
        int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
        
        // computing cost for each transformation
        int costReplace = cost[i - 1] + match;
        int costInsert = cost[i] + 1;
        int costDelete = newcost[i - 1] + 1;
        
        // keep minimum cost
        newcost[i] = Math.min(Math.min(costInsert, costDelete), costReplace);
      }
      
      // swap cost/newcost arrays
      int[] swap = cost;
      cost = newcost;
      newcost = swap;
    }
    
    // the distance is the cost for transforming all letters in both strings
    return cost[len0 - 1];
  }
  
  private Levenshtein() {

  }
  
}
