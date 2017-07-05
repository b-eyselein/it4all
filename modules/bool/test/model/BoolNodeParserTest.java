package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import model.node.BoolNode;
import model.token.Token;

public class BoolNodeParserTest {

  // @formatter:off
  private static final Map<String, String[]> FORMULAS_AND_TOKENS = ImmutableMap.of(
      "(a and b)", new String[]{"(", "a", "and", "b", ")"},
      "(a and b or c)", new String[]{"(", "a", "and", "b", "or", "c", ")"}
  );
  // @formatter:on
  
  // @Test(expected = CorrectionException.class)
  public void testKlammerung1() throws CorrectionException {
    BoolNodeParser.parseNew("(a and b");
  }
  
  // @Test(expected = StringIndexOutOfBoundsException.class)
  public void testKlammerung2() throws CorrectionException {
    BoolNodeParser.parseNew("a and b)");
  }
  
  // @Test(expected = CorrectionException.class)
  public void testKlammerung3() throws CorrectionException {
    BoolNodeParser.parseNew(")a and b(");
  }
  
  // @Test
  public void testNew() throws CorrectionException {
    String formula = "true";
    BoolNode tree = BoolNodeParser.parse(formula);
    assertThat(tree.toString(), equalTo("1"));
  }
  
  // @Test
  public void testParseNode() throws CorrectionException {
    String formula = "((a xor b) nor c) and ((a and b) xor c)";
    BoolNode tree = BoolNodeParser.parseNode(formula);
    assertNotNull(tree);
    assertEquals(tree.getAsString(false).toLowerCase(), formula);

    String formula2 = "not c";
    BoolNode tree2 = BoolNodeParser.parseNode(formula2);
    assertNotNull(tree2);
    assertEquals(tree2.getAsString(false).toLowerCase(), formula2);

    String formula3 = "b OR NOT (a OR b)";
    BoolNode tree3 = BoolNodeParser.parse(formula3);
    assertThat(tree3.toString(), equalTo(formula3));
  }
  
  // @Test(expected = CorrectionException.class)
  public void testParseNodeWithError() throws CorrectionException {
    String formula = "b andor c";
    BoolNodeParser.parseNode(formula);
  }
  
  // @Test
  public void testTokenize() {
    for(Map.Entry<String, String[]> formulaAndToken: FORMULAS_AND_TOKENS.entrySet()) {
      List<Token> tokens = BoolNodeParser.tokenize(formulaAndToken.getKey());
      assertThat(tokens.size(), equalTo(formulaAndToken.getValue().length));
      // FIXME: test if contains all...
      fail();
    }
  }
  
  // @Test(expected = CorrectionException.class)
  public void testunvollstaendigerAusdruck1() throws CorrectionException {
    BoolNodeParser.parseNew("a or");
  }
  
  // @Test(expected = CorrectionException.class)
  public void testunvollstaendigerAusdruck2() throws CorrectionException {
    BoolNodeParser.parseNew("a b");
  }
  
  // @Test(expected = StringIndexOutOfBoundsException.class)
  public void testunvollstaendigerAusdruck3() throws CorrectionException {
    BoolNodeParser.parseNew("a or ()");
  }
  
  // @Test(expected = CorrectionException.class)
  public void testunvollstaendigerAusdruck4() throws CorrectionException {
    BoolNodeParser.parseNew("a (or b)");
  }
}
