package model;

public enum NumberBase {

  // @formatter:off
  BINARY      (2,   "Binär",        "0b", "0-1*"),
  OCTAL       (8,   "Oktal",        "0o", "0-7"),
  HEXADECIMAL (16,  "Hexadezimal",  "0x", "0-9a-fA-F"),
  DECIMAL     (10,  "Dezimal",      "",   "1-9*");
  //@formatter:on

  private int base;

  private String systemName;
  private String mark;
  private String regex;

  private NumberBase(int theBase, String theBaseName, String theMark, String theRegex) {
    base = theBase;
    systemName = theBaseName;
    mark = theMark;
    regex = theRegex;
  }

  public static NumberBase getByBase(int base) {
    for(NumberBase type: NumberBase.values())
      if(type.base == base)
        return type;
    throw new IllegalArgumentException("There is NumberBase for base " + base);
  }

  public int getBase() {
    return base;
  }

  public String getHtmlPattern() {
    return "[\\s" + regex + "]*";
  }

  public String getMark() {
    return mark;
  }

  public String getPluralName() {
    return systemName + "zahlen";
  }

  public String getRegex() {
    return "-?" + mark + "[" + regex + "][" + regex + "]*";
  }

  public String getSingularName() {
    return systemName + "zahl";
  }

  @Override
  public String toString() {
    return systemName + "system";
  }

}
