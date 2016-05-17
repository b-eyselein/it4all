package model.spread;

import java.nio.file.Path;
import java.util.Arrays;

/**
 *
 * @author Stefan Olbrecht
 *
 */
public class SpreadSheetCorrector {
  
  public static SpreadSheetCorrectionResult correct(Path musterPath, Path testPath, boolean conditionalFormating,
      boolean charts) {
    
    String fileExtension = getExtension(testPath);
    if(fileExtension.equals("ods"))
      return (new ODFCorrector()).correct(musterPath, testPath, conditionalFormating, charts);
    else if(fileExtension.equals("xlsx") || fileExtension.equals("xlsm"))
      return (new XLSXCorrector()).correct(musterPath, testPath, conditionalFormating, charts);
    else
      return new SpreadSheetCorrectionResult(false,
          Arrays.asList("Falsche Dateiendung: \"" + fileExtension + "\". Korrektur konnte nicht gestartet werden."));
  }
  
  public static String getExtension(Path path) {
    return path.toString().substring(path.toAbsolutePath().toString().lastIndexOf(".") + 1).trim().toLowerCase();
  }
  
  public static String getFileName(Path path) {
    return path.toString().substring(path.toAbsolutePath().toString().lastIndexOf("/"),
        path.toAbsolutePath().toString().lastIndexOf("."));
  }
  
  public static String getUserFolder(Path path) {
    return path.toString().substring(0, path.toAbsolutePath().toString().lastIndexOf("/"));
  }
  
}
