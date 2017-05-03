package model;

public enum ProgLanguage {

  JS("Javascript", "javascript", "nashorn"), PYTHON("Python 2.7", "python", "python");

  private String name;
  private String aceName;
  private String engineName;

  private ProgLanguage(String theName, String theAceName, String theEngingeName) {
    name = theName;
    aceName = theAceName;
    engineName = theEngingeName;
  }

  public String getAceName() {
    return aceName;
  }

  public String getEngineName() {
    return engineName;
  }

  public String getName() {
    return name;
  }

}
