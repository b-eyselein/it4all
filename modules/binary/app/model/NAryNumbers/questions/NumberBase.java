package model.NAryNumbers.questions;

public enum NumberBase {
  
  // @formatter:off
  BINARY(2, "Binärsystem", "Binärzahl", "Binärzahlen"),
  OCTAL(8, "Oktalsystem", "Oktalzahl", "Oktalzahlen"),
  HEXADECIMAL(16, "Hexadezimalsystem", "Hexadezimalzahl", "Hexadezimalzahlen"),
  DECIMAL(10, "Dezimalsystem", "Dezimalzahl", "Dezimalzahlen");

  //@formatter:on
  public static NumberBase getByBase(int base) {
    for(NumberBase type: NumberBase.values())
      if(type.base == base)
        return type;
    throw new IllegalArgumentException("No QuestionType exists for base " + base);
  }
  
  private int base;
  private String baseName;
  private String nameSingular;
  private String namePlural;
  
  private NumberBase(int theBase, String theBaseName, String theNameSingular, String theNamePlural) {
    base = theBase;
    baseName = theBaseName;
    nameSingular = theNameSingular;
    namePlural = theNamePlural;
  }
  
  public int getBase() {
    return base;
  }

  public String getPluralName() {
    return namePlural;
  }

  public String getSingularName() {
    return nameSingular;
  }

  @Override
  public String toString() {
    return baseName;
  }
  
}
