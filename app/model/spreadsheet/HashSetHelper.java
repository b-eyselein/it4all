package model.spreadsheet;

import java.util.HashSet;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class HashSetHelper {
	
	public static String getSheetCFDiff(HashSet<String> hs1, HashSet<String> hs2) {
		HashSet<String> diff = HashSetHelper.getListDiff(hs1, hs2);
		String string = HashSetHelper.getMatchesOfCollection(diff);
		return string;
	}
	
	public static HashSet<String> getListDiff(HashSet<String> al1, HashSet<String> al2) {
		HashSet<String> diff = new HashSet<String>();
		diff.addAll(al1);
		diff.addAll(al2);
        diff.removeAll(al2);
        return diff;
	}
	
	public static String getMatchesOfCollection(HashSet<String> set) {
		String string = "";
		String[] coll = new String[set.size()];
		int index = 0;
		for (String item : set) {
			coll[index] = item;
			index++;
		}
		for (int i = 0; i < coll.length; i++) {
			string += coll[i];
			if (i == (coll.length - 2)) {
				string += " und ";
			} else if (i < (coll.length - 2)) {
				string += ", ";
			}
		}
		return string;
	}

}
