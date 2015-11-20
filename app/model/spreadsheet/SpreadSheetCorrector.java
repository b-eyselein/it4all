package model.spreadsheet;

import java.nio.file.Path;
import java.util.Arrays;

import model.spreadsheet.excel.XLSCorrector;
import model.spreadsheet.openoffice.ODFCorrector;

/**
 * 
 * @author Stefan Olbrecht
 *
 */
public class SpreadSheetCorrector {
  
  public static String correct(Path musterPath, Path testPath, boolean conditionalFormating, boolean charts) {
    String fileExtension = getExtension(testPath);
    if(fileExtension.equals("ods"))
      // FIXME: komplette Umstellung auf SpreadSheetCorrectionResult!
      return ODFCorrector.correct(musterPath, testPath, conditionalFormating, charts).getNotices().get(0);
    else if(fileExtension.equals("xlsx") || fileExtension.equals("xlsm"))
      return XLSCorrector.correct(musterPath, testPath, conditionalFormating, charts);
    else
      return new SpreadSheetCorrectionResult(false, Arrays.asList("Falsche Dateiendung: \"" + fileExtension
          + "\". Korrektur konnte nicht gestartet werden.")).getNotices().get(0);
  }
  
  public static String getUserFolder(Path path) {
    return path.toString().substring(0, path.toAbsolutePath().toString().lastIndexOf("/"));
  }
  
  public static String getFileName(Path path) {
    return path.toString().substring(path.toAbsolutePath().toString().lastIndexOf("/"),
        path.toAbsolutePath().toString().lastIndexOf("."));
  }
  
  public static String getExtension(Path path) {
    return path.toString().substring(path.toAbsolutePath().toString().lastIndexOf(".") + 1).trim().toLowerCase();
  }
  
}
