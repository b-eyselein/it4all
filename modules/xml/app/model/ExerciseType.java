package model;

public enum ExerciseType {
  XMLAgainstXSD("xml"), XMLAgainstDTD("xml"), XSDAgainstXML("xml"), DTDAgainstXML("dtd");
  
  public String studentFileEnding;
  
  private ExerciseType(String studentEnding) {
    this.studentFileEnding = studentEnding;
  }
}
