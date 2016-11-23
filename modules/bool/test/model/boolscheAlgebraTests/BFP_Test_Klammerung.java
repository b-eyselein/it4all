package model.boolscheAlgebraTests;

import org.junit.Test;

import model.BooleanParsingException;
import model.BoolescheFunktionParser;

public class BFP_Test_Klammerung {
  
  @Test(expected = BooleanParsingException.class)
  public void testKlammerung1() throws BooleanParsingException {
    BoolescheFunktionParser.parse("(a and b");
  }
  
  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testKlammerung2() throws BooleanParsingException {
    BoolescheFunktionParser.parse("a and b)");
  }
  
  @Test(expected = BooleanParsingException.class)
  public void testKlammerung3() throws BooleanParsingException {
    BoolescheFunktionParser.parse(")a and b(");
  }
  
  @Test(expected = BooleanParsingException.class)
  public void testunvollstaendigerAusdruck1() throws BooleanParsingException {
    BoolescheFunktionParser.parse("a or");
  }
  
  @Test(expected = BooleanParsingException.class)
  public void testunvollstaendigerAusdruck2() throws BooleanParsingException {
    BoolescheFunktionParser.parse("a b");
  }
  
  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testunvollstaendigerAusdruck3() throws BooleanParsingException {
    BoolescheFunktionParser.parse("a or ()");
  }
  
  @Test(expected = BooleanParsingException.class)
  public void testunvollstaendigerAusdruck4() throws BooleanParsingException {
    BoolescheFunktionParser.parse("a (or b)");
  }
  
}
