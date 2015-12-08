package model.spreadsheet;

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
  
  public static String getSheetCFDiff(HashSet<String> hs1, HashSet<String> hs2) {
    return HashSetHelper.getDifferenceOfCollections(hs1, hs2).toString();
  }
  
}
