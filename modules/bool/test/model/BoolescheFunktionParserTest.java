package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import model.node.BoolNode;
import model.token.Token;
import model.tree.BoolFormula;

public class BoolescheFunktionParserTest {

  // @formatter:off
  private static final Map<String, String[]> FORMULAS_AND_TOKENS = ImmutableMap.of(
      "(a and b)", new String[]{"(", "a", "and", "b", ")"},
      "(a and b or c)", new String[]{"(", "a", "and", "b", "or", "c", ")"}
  );
  // @formatter:on

  @Test(expected = BooleanParsingException.class)
  public void testKlammerung1() throws BooleanParsingException {
    BoolescheFunktionParser.parseNew("(a and b");
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testKlammerung2() throws BooleanParsingException {
    BoolescheFunktionParser.parseNew("a and b)");
  }

  @Test(expected = BooleanParsingException.class)
  public void testKlammerung3() throws BooleanParsingException {
    BoolescheFunktionParser.parseNew(")a and b(");
  }

  @Test
  public void testNew() throws BooleanParsingException {
    String formula = "true";
    BoolFormula tree = BoolescheFunktionParser.parse(formula);
    assertThat(tree.toString(), equalTo("1"));
  }

  @Test
  public void testParseNode() throws BooleanParsingException {
    String formula = "((a xor b) nor c) and ((a and b) xor c)";
    BoolNode tree = BoolescheFunktionParser.parseNode(formula);
    assertNotNull(tree);
    assertEquals(tree.getAsString(false).toLowerCase(), formula);

    String formula2 = "not c";
    BoolNode tree2 = BoolescheFunktionParser.parseNode(formula2);
    assertNotNull(tree2);
    assertEquals(tree2.getAsString(false).toLowerCase(), formula2);

    String formula3 = "b OR NOT (a OR b)";
    BoolFormula tree3 = BoolescheFunktionParser.parse(formula3);
    assertThat(tree3.toString(), equalTo(formula3));
  }

  @Test(expected = BooleanParsingException.class)
  public void testParseNodeWithError() throws BooleanParsingException {
    String formula = "b andor c";
    BoolescheFunktionParser.parseNode(formula);
  }

  @Test
  public void testTokenize() {
    for(Map.Entry<String, String[]> formulaAndToken: FORMULAS_AND_TOKENS.entrySet()) {
      List<Token> tokens = BoolescheFunktionParser.tokenize(formulaAndToken.getKey());
      assertThat(tokens.size(), equalTo(formulaAndToken.getValue().length));
      // FIXME: test if contains all...
      fail();
    }
  }

  @Test(expected = BooleanParsingException.class)
  public void testunvollstaendigerAusdruck1() throws BooleanParsingException {
    BoolescheFunktionParser.parseNew("a or");
  }

  @Test(expected = BooleanParsingException.class)
  public void testunvollstaendigerAusdruck2() throws BooleanParsingException {
    BoolescheFunktionParser.parseNew("a b");
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void testunvollstaendigerAusdruck3() throws BooleanParsingException {
    BoolescheFunktionParser.parseNew("a or ()");
  }

  @Test(expected = BooleanParsingException.class)
  public void testunvollstaendigerAusdruck4() throws BooleanParsingException {
    BoolescheFunktionParser.parseNew("a (or b)");
  }
}
