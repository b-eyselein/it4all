package model;

public enum Stage {

  // @formatter:off
  CLASS_SELECTION       ("classSelection"),
  DIAG_DRAWING_HELP     ("diagramDrawingHelp"),
  DIAG_DRAWING          ("diagramDrawing"),
  ATTRIB_METHOD_ASSIGN  ("todo...");
  // @formatter:on

  private String urlName;

  private Stage(String theUrlName) {
    urlName = theUrlName;
  }
  
  public Stage getByUrlName(String name) {
    for(Stage st: Stage.values())
      if(st.urlName.equals(name))
        return st;
    return null;
  }

}
