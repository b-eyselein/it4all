package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BinaryOperatorTest extends NodeTestBase {

  private static boolean[] invert(boolean[] array) {
    boolean[] inverted = new boolean[array.length];
    for(int i = 0; i < array.length; i++)
      inverted[i] = !array[i];
    return inverted;
  }

  private static void testContainedVariables(ScalaNode nodeUnderTest) {
    assertThat("The boolean formula " + nodeUnderTest + " should contain exactly 2 variables!",
        nodeUnderTest.getUsedVariables().size(), equalTo(2));

    testContainsVariable(nodeUnderTest, 'a');
    testContainsVariable(nodeUnderTest, 'b');
  }

  private static void testEvaluate(ScalaNode nodeUnderTest, boolean[] expected) {
    evalute(nodeUnderTest, FF, expected[0]);

    evalute(nodeUnderTest, FT, expected[1]);

    evalute(nodeUnderTest, TF, expected[2]);

    evalute(nodeUnderTest, TT, expected[3]);
  }

  private static void testNode(ScalaNode nodeUnderTest, boolean[] expected) {
    if(expected.length != 4)
      throw new IllegalArgumentException("There have to be exactly 4 expected boolean values!");

    testContainedVariables(nodeUnderTest);

    testEvaluate(nodeUnderTest, expected);

    testEvaluate(nodeUnderTest.negate(), invert(expected));

  }

  @Test
  public void testAnd() {
    testNode(ScalaNode.and(A, B), new boolean[]{false, false, false, true});
  }

  @Test
  public void testEquivalency() {
    testNode(ScalaNode.equiv(A, B), new boolean[]{true, false, false, true});
  }

  @Test
  public void testImplication() {
    testNode(ScalaNode.impl(A, B), new boolean[]{true, true, false, true});
  }

  @Test
  public void testNand() {
    testNode(ScalaNode.nand(A, B), new boolean[]{true, true, true, false});
  }

  @Test
  public void testNor() {
    testNode(ScalaNode.nor(A, B), new boolean[]{true, false, false, false});
  }

  @Test
  public void testOr() {
    testNode(ScalaNode.or(A, B), new boolean[]{false, true, true, true});
  }

  @Test
  public void testXor() {
    testNode(ScalaNode.xor(A, B), new boolean[]{false, true, true, false});
  }

}
