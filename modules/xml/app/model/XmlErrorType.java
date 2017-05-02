package model;

import model.exercise.Success;

public enum XmlErrorType {

  // @formatter:off
  FATALERROR  ("Fataler Fehler",        Success.NONE),
  ERROR       ("Fehler",                Success.NONE),
  WARNING     ("Warnung",               Success.PARTIALLY),
  NONE        ("Keine Fehler gefunden", Success.COMPLETE);
  // @formatter:on

  private String title;
  private Success success;

  private XmlErrorType(String theTitle, Success theSuccess) {
    title = theTitle;
    success = theSuccess;
  }

  public Success getSuccess() {
    return success;
  }

  public String getTitle() {
    return title;
  }

}