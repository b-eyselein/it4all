package model.boolscheAlgebraTests;

import org.junit.Test;

import model.BoolescheFunktionParser;

public class BFP_Test_Klammerung {
  
  @Test(expected = IllegalArgumentException.class)
  public void testKlammerung1() {
    BoolescheFunktionParser.parse("(a and b");
  }
  
  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testKlammerung2() {
    BoolescheFunktionParser.parse("a and b)");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testKlammerung3() {
    BoolescheFunktionParser.parse(")a and b(");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testunvollstaendigerAusdruck1() {
    BoolescheFunktionParser.parse("a or");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testunvollstaendigerAusdruck2() {
    BoolescheFunktionParser.parse("a b");
  }
  
  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testunvollstaendigerAusdruck3() {
    BoolescheFunktionParser.parse("a or ()");
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testunvollstaendigerAusdruck4() {
    BoolescheFunktionParser.parse("a (or b)");
  }
  
}
