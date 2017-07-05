package model.node;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;

import model.Assignment;

public class NodeTestBase {

  protected static final Variable A = new Variable('a');

  protected static final Variable B = new Variable('b');

  protected static final Assignment FF = new Assignment(ImmutableMap.of('a', false, 'b', false));

  protected static final Assignment FT = new Assignment(ImmutableMap.of('a', false, 'b', true));

  protected static final Assignment TF = new Assignment(ImmutableMap.of('a', true, 'b', false));

  protected static final Assignment TT = new Assignment(ImmutableMap.of('a', true, 'b', true));

  protected static void evalute(BoolNode node, Assignment assignment, boolean expected) {
    assertThat(
        "Evaluation von >>" + assignment.toString() + "<< auf Formel >>" + node + "<< sollte " + expected + " ergeben!",
        node.evaluate(assignment), equalTo(expected));
  }

  protected static void testContainsVariable(BoolNode nodeUnderTest, char variable) {
    assertTrue("The boolean formula " + nodeUnderTest + " should contain the variable '" + variable + "'!",
        nodeUnderTest.getUsedVariables().contains(Character.valueOf(variable)));

  }

}
