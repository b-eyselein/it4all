package model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

public class SpreadSheetCorrector {
  
  private static Map<String, SpreadCorrector<?, ?, ?, ?, ?>> correctors;
  
  static {
    correctors = new HashMap<>();
    SpreadCorrector<?, ?, ?, ?, ?> excel = new XLSXCorrector();
    
    correctors.put("ods", new ODFCorrector());
    correctors.put("xlsx", excel);
    correctors.put("xlsm", excel);
  }

  private SpreadSheetCorrector() {

  }
  
  public static SpreadSheetCorrectionResult correct(Path musterPath, Path testPath, boolean conditionalFormating,
      boolean charts) {
    
    String fileExtension = getExtension(testPath);
    
    SpreadCorrector<?, ?, ?, ?, ?> corrector = correctors.get(fileExtension);
    if(corrector == null)
      return new SpreadSheetCorrectionResult(false,
          "The filetype \"" + fileExtension + "\" is not supported. Could not start correction.");
    
    return corrector.correct(musterPath, testPath, conditionalFormating, charts);
  }
  
  public static String getExtension(Path path) {
    return getExtension(path.toString());
  }
  
  public static String getExtension(String path) {
    return FilenameUtils.getExtension(path);
  }
  
  public static String getFileName(Path path) {
    return FilenameUtils.getBaseName(path.toString());
  }
  
  public static String getUserFolder(Path path) {
    return FilenameUtils.getPathNoEndSeparator(path.toString());
  }
  
}
