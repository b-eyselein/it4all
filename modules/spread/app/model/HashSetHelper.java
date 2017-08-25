package model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Stefan Olbrecht
 *
 */
public class HashSetHelper {

  private HashSetHelper() {

  }

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
    HashSet<String> diff = new HashSet<>(master);
    diff.removeAll(compare);
    return diff;
  }

  public static String getDiffOfTwoFormulas(String string1, String string2) {
    String message = "";

    // Compare operators
    String operDiff = RegExpHelper.getCellOperatorsDiff(string1, string2);
    if(!operDiff.isEmpty())
      message += String.format(StringConsts.COMMENT_OPERATOR_MISSING_VAR, operDiff);

    // Compare ranges
    String rangeDiff = RegExpHelper.getCellRangesDiff(string1, string2);
    if(!rangeDiff.isEmpty())
      message += String.format(StringConsts.COMMENT_RANGE_MISSING_VAR, rangeDiff);

    // Compare formulas
    String formulaDiff = RegExpHelper.getCellFormulasDiff(string1, string2);
    if(!formulaDiff.isEmpty())
      message += String.format(StringConsts.COMMENT_FORMULA_HINT_VAR, formulaDiff);

    return message;
  }

  public static String getSheetCFDiff(Set<String> hs1, Set<String> hs2) {
    Collection<String> difference = getDifferenceOfCollections(hs1, hs2);
    return difference.isEmpty() ? "" : difference.toString();
  }

}
