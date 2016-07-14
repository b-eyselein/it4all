package model.NAryNumbers;

public enum NumberBase {
  
  // @formatter:off
  BINARY(       2,      "Binärsystem",        "Binärzahl",        "Binärzahlen",        "[\\s0-1]*"),
  OCTAL(        8,      "Oktalsystem",        "Oktalzahl",        "Oktalzahlen",        "[\\s0-7]*"),
  HEXADECIMAL(  16,     "Hexadezimalsystem",  "Hexadezimalzahl",  "Hexadezimalzahlen",  "[\\s0-9a-fA-F]*"),
  DECIMAL(      10,     "Dezimalsystem",      "Dezimalzahl",      "Dezimalzahlen",      "[\\s0-9]*");
  //@formatter:on
  
  public static NumberBase getByBase(int base) {
    for(NumberBase type: NumberBase.values())
      if(type.base == base)
        return type;
    throw new IllegalArgumentException("No QuestionType exists for base " + base);
  }
  
  private int base;
  private String systemName;
  private String nameSingular, namePlural;
  private String pattern;
  
  private NumberBase(int theBase, String theBaseName, String theNameSingular, String theNamePlural, String thePattern) {
    base = theBase;
    systemName = theBaseName;
    nameSingular = theNameSingular;
    namePlural = theNamePlural;
    pattern = thePattern;
  }
  
  public int getBase() {
    return base;
  }
  
  public String getPattern() {
    return pattern;
  }
  
  public String getPluralName() {
    return namePlural;
  }
  
  public String getSingularName() {
    return nameSingular;
  }
  
  @Override
  public String toString() {
    return systemName;
  }
  
}
