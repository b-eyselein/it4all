package model.NAryNumbers;

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
  private String pattern;

  private NumberBase(int theBase, String theBaseName, String theNameSingular, String theNamePlural) {
    base = theBase;
    baseName = theBaseName;
    nameSingular = theNameSingular;
    namePlural = theNamePlural;
    if(theBase == 2) {
    	pattern = "[\\s0-1]*";
    } else if (theBase == 8) {
    	pattern = "[\\s0-7]*";
    } else if (theBase == 10) {
    	pattern = "[\\s0-9]*";
    } else if (theBase == 16) {
    	pattern = "[\\s0-9a-fA-F]*";
    } else {
    	pattern ="[\\s0-9a-vA-V]*";
    }
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
    return baseName;
  }

}
