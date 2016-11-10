package model;

public enum XmlErrorType {
  
  FATALERROR("Fataler Fehler"), ERROR("Fehler"), WARNING("Warnung"), NONE("Keine Fehler gefunden");
  
  private String title;
  
  private XmlErrorType(String theTitle) {
    title = theTitle;
  }
  
  public String getTitle() {
    return title;
  }
  
}