package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.node.BoolNode;

public class BoolNodeParserTest {

  private static void validateParsing(String formula) throws CorrectionException {
    BoolNode tree = BoolNodeParser.parse(formula);

    assertNotNull("Parsing of formula " + formula + "should not be null!", tree);

    String parsedAsStr = tree.getAsString(false).toLowerCase();
    assertEquals("Parsing " + parsedAsStr + "should be equal to " + formula, parsedAsStr, formula);
  }

  @Test(expected = CorrectionException.class)
  public void testKlammerung1() throws CorrectionException {
    BoolNodeParser.parse("(a and b");
  }

  @Test(expected = CorrectionException.class)
  public void testKlammerung2() throws CorrectionException {
    BoolNodeParser.parse("a and b)");
  }

  @Test(expected = CorrectionException.class)
  public void testKlammerung3() throws CorrectionException {
    BoolNodeParser.parse(")a and b(");
  }

  @Test
  public void testNew() throws CorrectionException {
    String formula = "true";
    BoolNode tree = BoolNodeParser.parse(formula);
    assertThat(tree.toString(), equalTo("1"));
  }

  @Test
  public void testParse() throws CorrectionException {
    String formula = "((a xor b) nor c) and ((a and b) xor c)";
    validateParsing(formula);

    String formula2 = "not c";
    validateParsing(formula2);

    String formula3 = "b OR NOT (a OR b)";
    validateParsing(formula3);
  }

  @Test(expected = CorrectionException.class)
  public void testParseNodeWithError() throws CorrectionException {
    String formula = "b andor c";
    BoolNodeParser.parse(formula);
  }

  @Test(expected = CorrectionException.class)
  public void testParseWithoutOperator() throws CorrectionException {
    BoolNodeParser.parse("a b");
  }

  @Test(expected = CorrectionException.class)
  public void testunvollstaendigerAusdruck1() throws CorrectionException {
    BoolNodeParser.parse("a or");
  }

  @Test(expected = CorrectionException.class)
  public void testunvollstaendigerAusdruck3() throws CorrectionException {
    BoolNodeParser.parse("a or ()");
  }

  @Test(expected = CorrectionException.class)
  public void testunvollstaendigerAusdruck4() throws CorrectionException {
    BoolNodeParser.parse("a (or b)");
  }
}
