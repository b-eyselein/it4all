package model;

public enum NumberBase {

  // @formatter:off
  BINARY      (2,   "Binär",        "0b", "0-1",          "0-1"),
  OCTAL       (8,   "Oktal",        "0o", "1-7",        "0-7"),
  HEXADECIMAL (16,  "Hexadezimal",  "0x", "1-9a-fA-F",  "0-9a-fA-F"),
  DECIMAL     (10,  "Dezimal",      "",   "1-9",        "0-9");
  //@formatter:on

  private int base;

  private String systemName;
  private String mark;
  private String regexStart;
  private String regexRest;

  private NumberBase(int theBase, String theBaseName, String theMark, String theRegexStart, String theRegexRest) {
    base = theBase;
    systemName = theBaseName;
    mark = theMark;
    regexStart = theRegexStart;
    regexRest = theRegexRest;
  }

  public int getBase() {
    return base;
  }

  public String getHtmlPattern() {
    return "[\\s" + regexStart + "][\\s" + regexRest + "]*";
  }

  public String getMark() {
    return mark;
  }

  public String getPluralName() {
    return systemName + "zahlen";
  }

  public String getRegex() {
    return "-?" + mark + "[" + regexStart + "][" + regexRest + "]*";
  }

  public String getSingularName() {
    return systemName + "zahl";
  }

  public String getSystemName() {
    return systemName + "system";
  }

}
