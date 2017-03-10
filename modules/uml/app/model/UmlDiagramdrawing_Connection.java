package model;

public class UmlDiagramdrawing_Connection {
  
  String type;
  String start;
  String target;
  String mulstart;
  String multarget;
  
  public UmlDiagramdrawing_Connection(String type, String start, String target, String mulstart, String multarget) {
    this.type = type;
    this.start = start;
    this.target = target;
    this.mulstart = mulstart;
    this.multarget = multarget;
  }
  
  public String getMulstart() {
    return this.mulstart;
  }
  
  public String getMultarget() {
    return this.multarget;
  }
  
  public String getStart() {
    return this.start;
  }
  
  public String getTarget() {
    return this.target;
  }
  
  public String getType() {
    return this.type;
  }
  
}
