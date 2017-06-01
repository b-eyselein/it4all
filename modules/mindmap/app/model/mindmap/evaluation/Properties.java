package model.mindmap.evaluation;

import model.mindmap.evaluation.enums.MetaDataState;
import model.mindmap.evaluation.enums.Modus;

public class Properties {
  
  public enum ExportType {
    MINDMANAGER, LATEX, WORD;
  }
  
  private Modus modus = null;
  private MetaDataState mds = null;
  
  private ExportType exportType = null;
  
  public ExportType getExportType() {
    return exportType;
  }
  
  public MetaDataState getMDS() {
    return mds;
  }
  
  public Modus getModus() {
    return modus;
  }
  
  public void setExportType(ExportType exportType) {
    this.exportType = exportType;
  }
  
  public void setMDS(MetaDataState mds) {
    this.mds = mds;
  }
  
  public void setModus(Modus modus) {
    this.modus = modus;
  }
  
}
