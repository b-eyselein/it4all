package model;

public enum AvailableLanguages {
  
  // @formatter:off
  PYTHON_3  ("Python 3",    "python",     "python:3",     "sol.py",
      "if __name__ == '__main__':\n  n = int(input())"),

  JAVA_8    ("Java 8",      "java",       "8-jdk",        "sol.java",
      "public class Solution {\n\n  public static void main(String[] args) {\n    \n  }\n\n}");
  // @formatter:on
  
  private String name;
  private String aceName;
  private String imageName;
  private String scriptName;
  private String declaration;
  
  private AvailableLanguages(String theName, String theAceName, String theImageName, String theScriptName,
      String theDeclaration) {
    name = theName;
    aceName = theAceName;
    imageName = theImageName;
    scriptName = theScriptName;
    declaration = theDeclaration;
  }
  
  public static AvailableLanguages stdLang() {
    return PYTHON_3;
  }
  
  public String getAceName() {
    return aceName;
  }
  
  public String getDeclaration() {
    return declaration;
  }
  
  public String getImageName() {
    return imageName;
  }
  
  public String getName() {
    return name;
  }
  
  public String getScriptName() {
    return scriptName;
  }
  
}
