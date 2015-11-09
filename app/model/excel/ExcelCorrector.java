package model.excel;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class ExcelCorrector {
	
	static final String DIR = "files/";
	
	public static String startComparison(String musterPath, String testPath, boolean conditionalFormating, boolean charts) {
		String notice = "";
		if (ExcelCorrector.getExtension(testPath).equals("ods")) {
			notice = ODFCorrector.startComparison(musterPath, testPath, conditionalFormating, charts);
		} else {
			notice = XLSCorrector.startComparison(musterPath, testPath, conditionalFormating, charts);
		}
		return notice;
	}
	
	public static String getUserFolder(String string) {
		return string.substring(0, string.lastIndexOf("/"));
	}
	
	public static String getFileName(String string) {
		return string.substring(string.lastIndexOf("/"), string.lastIndexOf("."));
	}
	
	public static String getExtension(String string) {
		return string.substring(string.lastIndexOf(".") + 1).trim().toLowerCase();
	}

	public static void main(String[] args) {
		ExcelCorrector.startComparison(ExcelCorrector.DIR + "muster/", ExcelCorrector.DIR + "test/Aufgabe_Fahrrad.xlsx", false, false);
		System.out.println("Check: Done!");
	}

}
