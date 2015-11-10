package model.spreadsheet;

import model.spreadsheet.excel.XLSCorrector;
import model.spreadsheet.openoffice.ODFCorrector;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class SpreadSheetCorrector {
  
  static final String DIR = "files/";
  
  public static String startComparison(String musterPath, String testPath, String fileName, boolean conditionalFormating, boolean charts) {
    if(SpreadSheetCorrector.getExtension(testPath).equals("ods")) {
      return ODFCorrector.startComparison(musterPath, testPath, conditionalFormating, charts);
    } else if(SpreadSheetCorrector.getExtension(testPath).equals("xlsx")
        || SpreadSheetCorrector.getExtension(testPath).equals("xlsm")) {
      return XLSCorrector.startComparison(musterPath, testPath, conditionalFormating, charts);
    } else
      return "Falsche Dateiendung...";
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
  
  // public static void main(String[] args) {
  // ExcelCorrector.startComparison(ExcelCorrector.DIR + "muster/",
  // ExcelCorrector.DIR + "test/Aufgabe_Fahrrad.xlsx", false, false);
  // System.out.println("Check: Done!");
  // }
  
}
