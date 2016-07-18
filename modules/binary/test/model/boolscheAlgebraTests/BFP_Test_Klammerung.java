package model.boolscheAlgebraTests;

import org.junit.Test;

import model.boolescheAlgebra.BoolescheFunktionParser;

public class BFP_Test_Klammerung {

  private static final char[] ab = {'a', 'b'};

  @Test(expected = IllegalArgumentException.class)
  public void testKlammerung1() {
    BoolescheFunktionParser.parse("(a and b", ab);
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testKlammerung2() {
    BoolescheFunktionParser.parse("a and b)", ab);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testKlammerung3() {
    BoolescheFunktionParser.parse(")a and b(", ab);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testunvollstaendigerAusdruck1() {
    BoolescheFunktionParser.parse("a or", ab);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testunvollstaendigerAusdruck2() {
    BoolescheFunktionParser.parse("a b", ab);
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testunvollstaendigerAusdruck3() {
    BoolescheFunktionParser.parse("a or ()", ab);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testunvollstaendigerAusdruck4() {
    BoolescheFunktionParser.parse("a (or b)", ab);
  }

}
