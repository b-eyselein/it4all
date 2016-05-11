package model.spread;

import java.util.Collection;
import java.util.HashSet;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class HashSetHelper {
  
  /**
   * Computes all elements which are in the master Collection but which are not
   * in the compare Collection
   * 
   * @param master
   *          the first collection
   * @param compare
   *          the second collection
   * 
   * @return all elements which are not in compare but in master
   */
  public static Collection<String> getDifferenceOfCollections(Collection<String> master, Collection<String> compare) {
    HashSet<String> diff = new HashSet<String>(master);
    diff.removeAll(compare);
    return diff;
  }
  
  public static String getDiffOfTwoFormulas(String string1, String string2) {
    String message = "";
    
    // Compare operators
    String operDiff = RegExpHelper.getCellOperatorsDiff(string1, string2);
    if(operDiff != "")
      message += "Ein Operator (" + operDiff + ") fehlt.";
    
    // Compare ranges
    String rangeDiff = RegExpHelper.getCellRangesDiff(string1, string2);
    if(rangeDiff != "")
      message += "Der Bereich " + rangeDiff + " fehlt.";
    
    // Compare formulas
    String formulaDiff = RegExpHelper.getCellFormulasDiff(string1, string2);
    if(formulaDiff != "")
      message += "Verwenden Sie " + formulaDiff + ".";
    
    return message;
  }
  
  public static String getSheetCFDiff(HashSet<String> hs1, HashSet<String> hs2) {
    Collection<String> difference = getDifferenceOfCollections(hs1, hs2);
    if(difference.size() == 0)
      return "";
    else
      return difference.toString();
  }
  
}
