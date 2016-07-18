package model.boolescheAlgebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import model.boolescheAlgebra.BFTree.Node;

public class BoolescheFunktionParserTest {
  
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
