package model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
    
    String fileExtension = com.google.common.io.Files.getFileExtension(testPath.toString());
    
    SpreadCorrector<?, ?, ?, ?, ?> corrector = correctors.get(fileExtension);
    if(corrector == null)
      return new SpreadSheetCorrectionResult(false,
          "The filetype \"" + fileExtension + "\" is not supported. Could not start correction.");
    
    return corrector.correct(musterPath, testPath, conditionalFormating, charts);
  }
  
}
