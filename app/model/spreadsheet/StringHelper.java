package model.spreadsheet;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class StringHelper {
	
	public static String getDiffOfTwoFormulas(String string1, String string2) {
		String message = "";
		// Compare operators
		String operDiff = RegExpHelper.getCellOperatorsDiff(string1, string2);
		if (operDiff != "") {
			message += "Ein Operator (" + operDiff + ") fehlt.";
		} else {
			// Compare ranges
			String rangeDiff = RegExpHelper.getCellRangesDiff(string1, string2);
			if (rangeDiff != "") {
				message += "Der Bereich " + rangeDiff + " fehlt.";
			} else {
				// Compare formulas
				String formulaDiff = RegExpHelper.getCellFormulasDiff(string1, string2);
				if (formulaDiff != "") {
					message += "Verwenden Sie " + formulaDiff + ".";
				}
			}
		}
		return message;
	}

}
