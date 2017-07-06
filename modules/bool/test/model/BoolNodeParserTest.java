package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.node.BoolNode;

public class BoolNodeParserTest {

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
    BoolNode tree = BoolNodeParser.parse(formula);
    assertNotNull(tree);
    assertEquals(tree.getAsString(false).toLowerCase(), formula);

    String formula2 = "not c";
    BoolNode tree2 = BoolNodeParser.parse(formula2);
    assertNotNull(tree2);
    assertEquals(tree2.getAsString(false).toLowerCase(), formula2);

    String formula3 = "b OR NOT (a OR b)";
    BoolNode tree3 = BoolNodeParser.parse(formula3);
    assertThat(tree3.toString(), equalTo(formula3));
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
