package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import model.node.BinaryOperator.And;
import model.node.BinaryOperator.Equivalency;
import model.node.BinaryOperator.Implication;
import model.node.BinaryOperator.Nand;
import model.node.BinaryOperator.Nor;
import model.node.BinaryOperator.Or;
import model.node.BinaryOperator.Xor;

public class BinaryOperatorTest extends NodeTestBase {

  private static boolean[] invert(boolean[] array) {
    boolean[] inverted = new boolean[array.length];
    for(int i = 0; i < array.length; i++)
      inverted[i] = !array[i];
    return inverted;
  }

  private static void testContainedVariables(BoolNode nodeUnderTest) {
    assertThat("The boolean formula " + nodeUnderTest + " should contain exactly 2 variables!",
        nodeUnderTest.getUsedVariables().size(), equalTo(2));

    testContainsVariable(nodeUnderTest, 'a');
    testContainsVariable(nodeUnderTest, 'b');
  }

  private static void testEvaluate(BoolNode nodeUnderTest, boolean[] expected) {
    evalute(nodeUnderTest, FF, expected[0]);

    evalute(nodeUnderTest, FT, expected[1]);

    evalute(nodeUnderTest, TF, expected[2]);

    evalute(nodeUnderTest, TT, expected[3]);
  }

  private static void testNode(BoolNode nodeUnderTest, boolean[] expected) {
    if(expected.length != 4)
      throw new IllegalArgumentException("There have to be exactly 4 expected boolean values!");

    testContainedVariables(nodeUnderTest);

    testEvaluate(nodeUnderTest, expected);

    testEvaluate(nodeUnderTest.negate(), invert(expected));

  }

  @Test
  public void testAnd() {
    testNode(new And(A, B), new boolean[]{false, false, false, true});
  }

  @Test
  public void testEquivalency() {
    testNode(new Equivalency(A, B), new boolean[]{true, false, false, true});
  }

  @Test
  public void testImplication() {
    testNode(new Implication(A, B), new boolean[]{true, true, false, true});
  }

  @Test
  public void testNand() {
    testNode(new Nand(A, B), new boolean[]{true, true, true, false});
  }

  @Test
  public void testNor() {
    testNode(new Nor(A, B), new boolean[]{true, false, false, false});
  }

  @Test
  public void testOr() {
    testNode(new Or(A, B), new boolean[]{false, true, true, true});
  }

  @Test
  public void testXor() {
    testNode(new Xor(A, B), new boolean[]{false, true, true, false});
  }

}
