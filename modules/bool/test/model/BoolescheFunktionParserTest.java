package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.node.Node;
import model.tree.BoolescheFunktionTree;

public class BoolescheFunktionParserTest {

  @Test
  public void testNew() throws BooleanParsingException {
    String formula = "true";
    BoolescheFunktionTree tree = BoolescheFunktionParser.parse(formula);
    assertThat(tree.toString(), equalTo("1"));
  }

  @Test
  public void testParseNode() throws BooleanParsingException {
    String formula = "((a xor b) nor c) and ((a and b) xor c)";
    Node tree = BoolescheFunktionParser.parseNode(formula);
    assertNotNull(tree);
    assertEquals(tree.getAsString(false).toLowerCase(), formula);

    String formula2 = "not c";
    Node tree2 = BoolescheFunktionParser.parseNode(formula2);
    assertNotNull(tree2);
    assertEquals(tree2.getAsString(false).toLowerCase(), formula2);
    
    String formula3 = "b OR NOT (a OR b)";
    BoolescheFunktionTree tree3 = BoolescheFunktionParser.parse(formula3);
    assertThat(tree3.toString(), equalTo(formula3));
  }

  @Test(expected = BooleanParsingException.class)
  public void testParseNodeWithError() throws BooleanParsingException {
    String formula = "b andor c";
    BoolescheFunktionParser.parseNode(formula);
  }

}
