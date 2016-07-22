package model.bool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import model.bool.BoolescheFunktionParser;
import model.bool.node.Node;
import model.bool.tree.BoolescheFunktionTree;

public class BoolescheFunktionParserTest {

  @Test
  public void testNew() {
    String formula = "b OR NOT (a OR b)";
    BoolescheFunktionTree tree = BoolescheFunktionParser.parse(formula);
    assertThat(tree.toString(), equalTo(formula));
  }

  @Test
  public void testParseNode() {
    String formula = "((a xor b) nor c) and ((a and b) xor c)";
    Node tree = BoolescheFunktionParser.parseNode(formula);
    assertNotNull(tree);
    assertEquals(tree.getAsString(false).toLowerCase(), formula);

    String formula2 = "not c";
    Node tree2 = BoolescheFunktionParser.parseNode(formula2);
    assertNotNull(tree2);
    assertEquals(tree2.getAsString(false).toLowerCase(), formula2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseNodeWithError() {
    String formula = "b andor c";
    BoolescheFunktionParser.parseNode(formula);
  }

}
