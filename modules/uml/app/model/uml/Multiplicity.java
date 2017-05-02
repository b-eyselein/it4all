package model.uml;

public enum Multiplicity {
  
  SINGLE('1'), UNBOUND('*');
  
  private char representant;
  
  private Multiplicity(char theRepresentant) {
    representant = theRepresentant;
  }
  
  public static Multiplicity getByRepresentant(char representant) {
    if(representant == '1')
      return SINGLE;
    else if(representant == '*')
      return UNBOUND;
    else
      throw new IllegalArgumentException("There is no Multiplicity for representant " + representant);
  }
  
  public char getRepresentant() {
    return representant;
  }
}